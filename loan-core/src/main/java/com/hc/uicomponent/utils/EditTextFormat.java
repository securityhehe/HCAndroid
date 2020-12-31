package com.hc.uicomponent.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import frame.utils.RegularUtil;

/**
 * Author: TinhoXu
 * E-mail: xth@erongdu.com
 * Date: 2016/2/23 15:18
 * <p/>
 * Description: 输入框格式化
 */
public class EditTextFormat {
    /** 允许输入的整数的最大位数 */
    private static int number_length  = 8;
    /** 允许输入的小数的最大位数 */
    private static int decimal_length = 2;

    public interface EditTextFormatWatcher {
        void OnTextWatcher(String str);
    }

    private static void addSpaceToString(final EditText mEditText, final EditTextFormatWatcher watcher, final int[] rules) {
        mEditText.addTextChangedListener(new TextWatcher() {
            int beforeTextLength = 0;
            int onTextLength = 0;
            boolean isChanged = false;
            // 记录光标的位置
            int location = 0;
            private char[] tempChar;
            private StringBuffer buffer = new StringBuffer();
            // 空格数量
            int blankNumber = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTextLength = s.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                blankNumber = 0;
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == ' ') {
                        blankNumber++;
                        break;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTextLength = s.length();
                buffer.append(s.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isChanged) {
                    location = mEditText.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int blankNumber = 0;
                    while (index < buffer.length()) {
                        for (int rule : rules) {
                            if (index == rule) {
                                buffer.insert(index, ' ');
                                blankNumber++;
                            }
                        }
                        index++;
                    }

                    if (blankNumber > this.blankNumber) {
                        location += (blankNumber - this.blankNumber);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }

                    // s.replace(0, s.length(), str);
                    mEditText.setText(str);
                    Selection.setSelection(mEditText.getText(), location);
                    isChanged = false;
                    if (watcher != null) {
                        watcher.OnTextWatcher(s.toString());
                    }
                }
            }
        });
    }

    /** 银行卡号码的格式 */
    public static void bankCardNumAddSpace(final EditText mEditText, final EditTextFormatWatcher watcher) {
        addSpaceToString(mEditText, watcher, new int[]{4, 9, 14, 19});
    }

    /** 手机号码的格式 */
    public static void phoneNumAddSpace(final EditText mEditText, final EditTextFormatWatcher watcher) {
        addSpaceToString(mEditText, watcher, new int[]{3, 8});
    }

    /** 身份证的格式 */
    public static void IDCardNumAddSpace(final EditText mEditText, final EditTextFormatWatcher watcher) {
        addSpaceToString(mEditText, watcher, new int[]{6, 11, 16});
    }

    /**
     * 去除字符串中的空格
     */
    public static String getTrimStr(String str) {
        return str.replaceAll("\\s*", "");
    }

    /**
     * 限制EditText 最多只能输入 numberLength 位，第 numberLength+1 位如果不是"."，则不允许输入
     * <p/>
     * E.G.
     * view.setFilters(new InputFilter[]{EditTextFormat.getDecimalFilter()});
     *
     * @param numberLength
     *         整数长度限制
     * @param decimalLength
     *         小数长度限制
     */
    public static InputFilter getDecimalFilter(int numberLength, int decimalLength) {
        number_length = numberLength;
        decimal_length = decimalLength;
        return decimalFilter;
    }

    /**
     * 限制EditText 百分比输入控制
     */
    public static InputFilter getPercentFilter() {
        return percentFilter;
    }

    /**
     * 限制EditText不能输入空格
     */
    public static InputFilter getBlankFilter() {
        return blankFilter;
    }

    /**
     * 限制EditText只能输入数字和Xx
     */
    public static InputFilter getIDCardFilter() {
        return IDCardFilter;
    }

    /**
     * 限制EditText只能输入数字和字母
     */
    public static InputFilter getPasswordFilter() {
        return passwordFilter;
    }

    /**
     * 限制EditText只能输入正整数
     */
    public static InputFilter getNumberFilter() {
        return numberFilter;
    }

    /**
     * 限制EditText不能输入数字
     */
    public static InputFilter getNoNumberFilter() {
        return noNumberFilter;
    }
    /**
     * 限制EditText只能输入字母和空格
     */
    public static InputFilter getSpaceAndLetterFilter() {
        return spaceAndLetterFilter;
    }

    /**
     * 限制EditText不能输入表情 begin --------->>>
     */
    public static InputFilter getEmojiExcludeFilter() {
        return emojiExcludeFilter;
    }

    private static InputFilter emojiExcludeFilter = new InputFilter(){
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
//                    ToastUtil.toast("不能输入表情");
                    return "";
                }
            }
            return null;
        }
    };

    //限制EditText不能输入表情 end --------->>>


    /**
     * 添加新的Filter
     */
    public static void addFilter(EditText view, InputFilter filter) {
        InputFilter[] old      = view.getFilters();
        InputFilter[] filters  = new InputFilter[old.length + 1];
        int           position = 0;
        for (; position < old.length; position++) {
            filters[position] = old[position];
        }
        filters[position] = filter;
        view.setFilters(filters);
    }

    /** 数值输入控制 */
    private static InputFilter decimalFilter  = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            // ^(([1-9]\d{0,8})|0)(\.\d{0,2})?$
            if (RegularUtil.isMatcher(dest.toString() + source.toString(),
                    "^(([1-9]\\d{0," + (number_length - 1) + "})|0)(\\.\\d{0," + decimal_length + "})?$")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 百分比输入控制 */
    private static InputFilter percentFilter  = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(dest.toString() + source.toString(), "^100|[1-9]?\\d(\\.\\d{0,2})?$")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 不允许输入空格 */
    private static InputFilter blankFilter    = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(dest.toString() + source.toString(), "^[^ ]+$")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 身份证输入限制 */
    private static InputFilter IDCardFilter   = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(dest.toString() + source.toString(), "(\\d{0,18})|(\\d{0,17}[Xx]?)")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 数字和字母输入限制 */
    private static InputFilter passwordFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(source.toString(), "^[A-Za-z0-9]+")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 正整数输入限制 */
    private static InputFilter numberFilter   = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(source.toString(), "^[0-9]\\d*")) {
                return null;
            } else {
                return "";
            }
        }
    };
    /** 仅能输入英文字符和空格输入限制 */
    private static InputFilter spaceAndLetterFilter   = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(source.toString(), "^[a-zA-z ]*")) {
                return null;
            } else {
                return "";
            }
        }
    };

    /** 不能输入数字 **/
    private static InputFilter noNumberFilter   = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dStart, int dEnd) {
            if (RegularUtil.isMatcher(source.toString(), "^[0-9]\\d*")) {
                return "";
            } else {
                return null;
            }
        }
    };
}
