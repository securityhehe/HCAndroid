package com.wildma.idcardcamera.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.ToastUtils;
import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.global.RuntimeConfig;
import com.wildma.idcardcamera.utils.CommonUtils;
import com.wildma.idcardcamera.utils.FileUtils;
import com.wildma.idcardcamera.utils.ImageUtils;
import com.wildma.idcardcamera.utils.PermissionUtils;
import com.wildma.idcardcamera.utils.ScreenUtils;

import java.io.ByteArrayOutputStream;

import frame.utils.StorageUtils;


/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${拍照界面}
 */
public class CameraActivity extends Activity implements View.OnClickListener {

//    private CropImageView mCropImageView;

    private ImageView     mShowCropImageView;//TODO 显示出最终裁剪的Bitmap
    private Bitmap        mCropBitmap;
    private CameraPreview mCameraPreview;
    private View          mLlCameraCropContainer;
    private ImageView     mIvCameraCrop;//TODO 可预览出的提示的背景图片（提示：正面和背面的图片引导线）
    private ImageView     mIvCameraFlash;
    private View          mLlCameraOption;
    private View          mLlCameraResult;
//    private TextView      mViewCameraCropBottom;
    private FrameLayout   mFlCameraOption;
    private View          mViewCameraCropLeft;

    private int     mType;//拍摄类型
    private boolean isToast = true;//是否弹吐司，为了保证for循环只弹一次

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*动态请求需要的权限*/
        boolean checkPermissionFirst = PermissionUtils.checkPermissionFirst(this, IDCardCamera.PERMISSION_CODE_FIRST,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (checkPermissionFirst) {
            init();
        }
    }

    /**
     * 处理请求权限的响应
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求权限结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissions = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) { //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(this, "Please manually open the permissions required for the app！", Toast.LENGTH_SHORT).show();
                        isToast = false;
                    }
                }
            }
        }
        isToast = true;
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限");
            init();
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许");
            finish();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void init() {
        setContentView(R.layout.activity_camera);
        mType = getIntent().getIntExtra(IDCardCamera.TAKE_TYPE, 0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView();
        initListener();
    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mLlCameraCropContainer = findViewById(R.id.ll_camera_crop_container);
        mIvCameraCrop = (ImageView) findViewById(R.id.iv_camera_crop);
        mIvCameraFlash = (ImageView) findViewById(R.id.iv_camera_flash);
        mLlCameraOption = findViewById(R.id.ll_camera_option);
        mLlCameraResult = findViewById(R.id.ll_camera_result);
//        mCropImageView = findViewById(R.id.crop_image_view);
        mShowCropImageView = findViewById(R.id.img_crop);
//        mViewCameraCropBottom = (TextView) findViewById(R.id.view_camera_crop_bottom);
        mFlCameraOption = (FrameLayout) findViewById(R.id.fl_camera_option);
        mViewCameraCropLeft = findViewById(R.id.view_camera_crop_left);

        float screenMinSize = Math.min(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));//TODO screenMinSize ->高度
        float screenMaxSize = Math.max(ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this));//TODO screenMinSize ->宽度
        float height = (int) (screenMinSize * 0.75);//TODO 确定下来的高度
        float width = (int) (height * 75.0f / 47.0f);//TODO 确定下来的宽度

        System.out.println("拍照：初始化的可视化的区域大小[" + width +"," + height + "']");

        //获取底部"操作区域"的宽度
        float flCameraOptionWidth = (screenMaxSize - width) / 2;

        //TODO 确定出裁剪的区域的容器
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        mLlCameraCropContainer.setLayoutParams(containerParams);

        //TODO 裁剪图片的大小
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        mIvCameraCrop.setLayoutParams(cropParams);

        LinearLayout.LayoutParams cameraOptionParams = new LinearLayout.LayoutParams((int) flCameraOptionWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        //获取"相机裁剪区域"的宽度来动态设置底部"操作区域"的宽度，使"相机裁剪区域"居中
        mFlCameraOption.setLayoutParams(cameraOptionParams);
        switch (mType) {
            case IDCardCamera.TYPE_IDCARD_FRONT:
                mIvCameraCrop.setImageResource(R.mipmap.in_kyc_aadhaar_front_guid_line);
                break;
            case IDCardCamera.TYPE_IDCARD_BACK:
                mIvCameraCrop.setImageResource(R.mipmap.in_kyc_aadhaar_back_guid_line);
                break;
            case IDCardCamera.TYPE_PAN_FRONT:
                mIvCameraCrop.setImageResource(R.mipmap.in_kyc_pan_guid_line);
                break;
        }

        /*增加0.5秒过渡界面，解决个别手机首次申请权限导致预览界面启动慢的问题*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCameraPreview.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 500);
    }

    private void initListener() {
        mCameraPreview.setOnClickListener(this);
        mIvCameraFlash.setOnClickListener(this);
        findViewById(R.id.iv_camera_close).setOnClickListener(this);
        findViewById(R.id.iv_camera_take).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_ok).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_preview) {
            mCameraPreview.focus();
        } else if (id == R.id.iv_camera_close) {
            finish();
        } else if (id == R.id.iv_camera_take) {
            if (!CommonUtils.isFastClick()) {
                takePhoto();
            }
        } else if (id == R.id.iv_camera_flash) {
            if (CameraUtils.hasFlash(this)) {
                boolean isFlashOn = mCameraPreview.switchFlashLight();
                mIvCameraFlash.setImageResource(isFlashOn ? R.mipmap.in_kyc_flash_off : R.mipmap.in_kyc_flash_on);
            } else {
                Toast.makeText(this, R.string.no_flash, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_camera_result_ok) {
            confirm();
        } else if (id == R.id.iv_camera_result_cancel) {
            mCameraPreview.setEnabled(true);
            mCameraPreview.addCallback();
            mCameraPreview.startPreview();
            mIvCameraFlash.setImageResource(R.mipmap.in_kyc_flash_on);
            setTakePhotoLayout();
        }
    }

    /**
     * 拍照 //TODO 触发拍照，获取到捕获的Bitmap
     */
    private void takePhoto() {
        mCameraPreview.setEnabled(false);
        try {
            CameraUtils.getCamera().setOneShotPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(final byte[] bytes, Camera camera) {
                    final Camera.Size size = camera.getParameters().getPreviewSize(); //获取预览大小
                    camera.stopPreview();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final int w = size.width;
                            final int h = size.height;
                            Bitmap bitmap = ImageUtils.getBitmapFromByte(bytes, w, h);//TODO 获取到拍照的图片 by zw

                            System.out.println("拍照：捕获到的可视化的图片大小[" + bitmap.getWidth() +"," + bitmap.getHeight() + "']");
                            cropImage(bitmap);
                        }
                    }).start();
                }
            });
        } catch (Exception e) {
            ToastUtils.showLong("Sorry,there was a problem, please try again later or give feedback");
        }
    }

    /**
     * 裁剪图片
     */
    private void cropImage(Bitmap bitmap) {
        /*计算扫描框的坐标点*/ //TODO 确定图片相对[整个屏幕]大小区域的坐标点
        float left = mViewCameraCropLeft.getWidth();
        float top = mIvCameraCrop.getTop();
        float right = mIvCameraCrop.getRight() + left;
        float bottom = mIvCameraCrop.getBottom();

        /*计算扫描框坐标点占原图坐标点的比例*/ //TODO 这个预览的View对应的整个屏幕比例
        float leftProportion = left / mCameraPreview.getWidth();
        float topProportion = top / mCameraPreview.getHeight();
        float rightProportion = right / mCameraPreview.getWidth();
        float bottomProportion = bottom / mCameraPreview.getBottom();

        /*自动裁剪*/
        mCropBitmap = Bitmap.createBitmap(bitmap,
                (int) (leftProportion * (float) bitmap.getWidth()),
                (int) (topProportion * (float) bitmap.getHeight()),
                (int) ((rightProportion - leftProportion) * (float) bitmap.getWidth()),
                (int) ((bottomProportion - topProportion) * (float) bitmap.getHeight()));

        System.out.println("拍照：裁剪后的图片大小[" + mCropBitmap.getWidth() +"," + mCropBitmap.getHeight() + ",size=" + mCropBitmap.getByteCount() / 1024 /1024 + "]");

        mCropBitmap = compressBySampleSize(mCropBitmap,1280,741,false);

        System.out.println("拍照：压缩后的图片大小[" + mCropBitmap.getWidth() +"," + mCropBitmap.getHeight() + ",size byte=" + mCropBitmap.getByteCount()+ ",kb=" + mCropBitmap.getByteCount() / 1024 + ",mb="+ mCropBitmap.getByteCount()/ 1024/1024 +"]");



        /*设置成手动裁剪模式*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //将手动裁剪区域设置成与扫描框一样大
                mShowCropImageView.setLayoutParams(new LinearLayout.LayoutParams(mIvCameraCrop.getWidth(), mIvCameraCrop.getHeight()));
                setCropLayout();
                mShowCropImageView.setImageBitmap(mCropBitmap);
            }
        });
    }

    /**
     * 设置裁剪布局
     */
    private void setCropLayout() {
        mIvCameraCrop.setVisibility(View.GONE);
        mCameraPreview.setVisibility(View.GONE);
        mLlCameraOption.setVisibility(View.GONE);

        mShowCropImageView.setVisibility(View.VISIBLE);//TODO 将裁剪View注释掉
        mLlCameraResult.setVisibility(View.VISIBLE);
//        mViewCameraCropBottom.setText("");
    }

    /**
     * 设置拍照布局
     */
    private void setTakePhotoLayout() {
        mIvCameraCrop.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mLlCameraOption.setVisibility(View.VISIBLE);
        mShowCropImageView.setVisibility(View.GONE);
        mLlCameraResult.setVisibility(View.GONE);
//        mViewCameraCropBottom.setText(getString(R.string.touch_to_focus));

        mCameraPreview.focus();
    }

    /**
     * 点击确认，返回图片路径 //TODO 缓存native
     */
    @SuppressLint("ObsoleteSdkInt")
    private void confirm() {
        /*保存图片到sdcard并返回图片路径*/
        if (FileUtils.createOrExistsDir(RuntimeConfig.TEMP_PHOTO_PATH)) {
            StringBuffer buffer = new StringBuffer();
            String imagePath = "";
            if (mType == IDCardCamera.TYPE_IDCARD_FRONT) {
                imagePath = buffer.append(RuntimeConfig.TEMP_PHOTO_PATH).append(IDCardCamera.ID_CARD_FRONT_IMG).toString();
            } else if (mType == IDCardCamera.TYPE_IDCARD_BACK) {
                imagePath = buffer.append(RuntimeConfig.TEMP_PHOTO_PATH).append(IDCardCamera.ID_CARD_BACK_IMG).toString();
            } else if (mType == IDCardCamera.TYPE_PAN_FRONT) {
                imagePath = buffer.append(RuntimeConfig.TEMP_PHOTO_PATH).append(IDCardCamera.PAN_CARD_FORNT_IMG).toString();
            }

            //TODO 此处需要处理拍照的图片保存路径
            if (ImageUtils.save(mCropBitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                Intent intent = new Intent();
                intent.putExtra(IDCardCamera.IMAGE_PATH, imagePath);
                setResult(IDCardCamera.RESULT_CODE, intent);
                finish();
            }
        } else {
            if (!PermissionUtils.checkPermissionFirst(this,
                    IDCardCamera.PERMISSION_CODE_FIRST,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA})) {
                ToastUtils.showShort("You must give the necessary permissions!");

                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ToastUtils.showShort("Sorry,your phone maybe not adaptation,please feedback");
                return;
            }

            if (StorageUtils.queryAvailableSize() <= mCropBitmap.getByteCount()) {
                ToastUtils.showShort("Sorry, your phone has no more space");
                return;
            }
            ToastUtils.showShort("Sorry,there occur a problem we don't know,please feedback");
        }
    }

    public static Bitmap compressBySampleSize(final Bitmap src,
                                              final int maxWidth,
                                              final int maxHeight,
                                              final boolean recycle) {
        if (src == null || src.getWidth() == 0 || src.getHeight() == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);//不压缩质量
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraPreview != null) {
            mCameraPreview.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}