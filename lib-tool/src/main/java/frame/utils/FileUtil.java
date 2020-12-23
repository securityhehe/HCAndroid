package frame.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/7/28 18:04
 * <p>
 * Description: 文件操作工具类
 */
@SuppressWarnings("unused")
public class FileUtil {
    private static Integer BUFFER_SIZE = 1024 * 1024 * 10;

    /**
     * 获取文件的行数
     */
    public static int countLines(File file) throws IOException {
        InputStream is    = null;
        int         count = 0;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] c = new byte[BUFFER_SIZE];
            int    readChars;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return count;
    }

    /**
     * 以列表的方式获取文件的所有行
     */
    public static List<String> lines(File file) {
        BufferedReader reader = null;
        List<String>   list   = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的所有行
     *
     * @param file
     *         文件
     * @param encoding
     *         指定读取文件的编码
     */
    public static List<String> lines(File file, String encoding) {
        BufferedReader reader = null;
        List<String>   list   = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的指定的行数数据
     */
    public static List<String> lines(File file, int lines) {
        BufferedReader reader = null;
        List<String>   list   = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (list.size() == lines) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 以列表的方式获取文件的指定的行数数据
     *
     * @param file
     *         文件
     * @param lines
     *         起始行数
     * @param encoding
     *         指定读取文件的编码
     */
    public static List<String> lines(File file, int lines, String encoding) {
        BufferedReader reader = null;
        List<String>   list   = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
                if (list.size() == lines) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 在文件末尾追加一行
     *
     * @param file
     *         文件
     * @param str
     *         内容
     */
    public static boolean appendLine(File file, String str) {
        RandomAccessFile randomFile    = null;
        String           lineSeparator = System.getProperty("line.separator", "\n");
        try {
            randomFile = new RandomAccessFile(file, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(lineSeparator + str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != randomFile) {
                    randomFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 在文件末尾追加一行
     *
     * @param file
     *         文件
     * @param str
     *         内容
     * @param encoding
     *         指定写入的编码
     */
    public static boolean appendLine(File file, String str, String encoding) {
        RandomAccessFile randomFile    = null;
        String           lineSeparator = System.getProperty("line.separator", "\n");
        try {
            randomFile = new RandomAccessFile(file, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write((lineSeparator + str).getBytes(encoding));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != randomFile) {
                    randomFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 快速清空一个超大的文件
     */
    public static boolean cleanFile(File file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write("");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取文件的Mime类型
     */
    public static String mimeType(String file) throws IOException {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(file);
    }


    /**
     * 获取文件最后的修改时间
     */
    public static Date modifyTime(File file) {
        return new Date(file.lastModified());
    }

    /**
     * 获取文件的Hash
     */
    public static String hash(File file) {
        return MDUtil.FileMD(MDUtil.TYPE.MD5, file);
    }

    /**
     * 复制文件
     */
    public static boolean copy(String sourcePath, String targetPath) {
        File file = new File(sourcePath);
        return copy(file, targetPath);
    }

    /**
     * 复制文件
     * 通过该方式复制文件文件越大速度越是明显
     */
    public static boolean copy(File sourceFile, String targetFile) {
        FileInputStream  inputStream  = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(new File(targetFile));
            FileChannel in  = inputStream.getChannel();
            FileChannel out = outputStream.getChannel();
            //设定缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (in.read(buffer) != -1) {
                buffer.flip();//准备写入，防止其他读取，锁住文件
                out.write(buffer);
                buffer.clear();//准备读取。将缓冲区清理完毕，移动文件内部指针
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }

                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 创建多级目录
     */
    public static void createPaths(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 创建文件支持多级目录
     */
    public static boolean createFiles(String path) {
        File file = new File(path);
        File dir  = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除一个文件
     */
    public static boolean deleteFile(File file) {
        return file.delete();
    }

    /**
     * 删除一个目录(ok)
     */
    public static boolean deleteDir(File file) {
        System.out.println("--->清除目录中--->" + file.getAbsolutePath() + "<------------");
        List<File> files = listFileAll(file);
        if (null != files) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    deleteFile(f);
                }
            }
        }
        return file.delete();
    }

    /**
     * 快速的删除超大的文件
     */
    public static boolean deleteBigFile(File file) {
        return cleanFile(file) && file.delete();
    }

    /**
     * 罗列指定路径下的全部文件
     *
     * @param path
     *         路径
     */
    public static List<File> listFile(String path) {
        File file = new File(path);
        return listFile(file);
    }

    /**
     * 复制目录
     *
     * @param sourcePath
     *         源路径
     * @param targetPath
     *         目标路径
     */
    public static void copyDir(String sourcePath, String targetPath) {
        System.out.println("--->复制目录中---src->" + sourcePath + "<-------dest->" + targetPath);
        File file = new File(sourcePath);
        copyDir(file, targetPath);
    }

    /**
     * 复制目录
     *
     * @param sourceFile
     *         源路径
     * @param targetPath
     *         目标路径
     */
    public static void copyDir(File sourceFile, String targetPath) {
        File targetFile = new File(targetPath);
        if (!targetFile.exists()) {
            createPaths(targetPath);
        }
        File[] files = sourceFile.listFiles();
        if (null != files) {
            for (File file : files) {
                String path = file.getName();
                if (file.isDirectory()) {
                    copyDir(file, targetPath + "/" + path);
                } else {
                    copy(file, targetPath + "/" + path);
                }
            }
        }
    }

    /**
     * 罗列指定路径下的全部文件
     */
    public static List<File> listFile(File path) {
        List<File> list  = new ArrayList<>();
        File[]     files = path.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFile(file));
                } else {
                    list.add(file);
                }
            }
        }
        return list;
    }

    /**
     * 罗列指定路径下的全部文件包括文件夹
     */
    public static List<File> listFileAll(File path) {
        List<File> list  = new ArrayList<>();
        File[]     files = path.listFiles();
        if (null != files) {
            for (File file : files) {
                list.add(file);
                if (file.isDirectory()) {
                    list.addAll(listFileAll(file));
                }
            }
        }
        return list;
    }

    /**
     * 获取指定目录下的特点文件,通过后缀名过
     */
    public static List<File> listFileFilter(File dirPath, final String postfix) {
        List<File> list  = new ArrayList<>();
        File[]     files = dirPath.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(listFileFilter(file, postfix));
                } else {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(postfix.toLowerCase())) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 在指定的目录下搜寻文个文件
     */
    public static List<File> searchFile(File dirPath, String fileName) {
        List<File> list  = new ArrayList<>();
        File[]     files = dirPath.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(searchFile(file, fileName));
                } else {
                    String Name = file.getName().toLowerCase();
                    if (Name.equals(fileName)) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查找符合正则表达式reg的的文件
     */
    public static List<File> searchFileReg(File dirPath, String reg) {
        List<File> list  = new ArrayList<>();
        File[]     files = dirPath.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(searchFile(file, reg));
                } else {
                    String Name = file.getName();
                    if (RegularUtil.isMatcher(Name, reg)) {
                        list.add(file);
                    }
                }
            }
        }
        return list;
    }


    /**
     * 保存图片到本地
     */
    public static String saveFile(Context context, String filePath, String fileName, byte[] bytes) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (bytes == null || bytes.length == 0){
            return "";
        }

        String           fileFullName = "";
        FileOutputStream fos          = null;
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                return null;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            if (fullFile.exists()) {
                fullFile.delete();
            }
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            fileFullName = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = "";
                }
            }
        }
        return fileFullName;
    }

    /**
     * 保存文件
     */
    public static String saveFile(Context context, String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = BitmapUtil.bitmapToBytes(bitmap);
        return saveFile(context, filePath, fileName, bytes);
    }


    ////////////////////////////
    /**
     * 根据地址获得数据的字节流
     * @param strUrl 网络连接地址
     * @return
     */
    public static byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while( (len=inStream.read(buffer)) != -1 ){
                outStream.write(buffer, 0, len);
            }
            inStream.close();
            return outStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
