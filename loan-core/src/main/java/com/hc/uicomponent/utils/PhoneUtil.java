package com.hc.uicomponent.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;


import com.hc.data.sms.SmsInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import frame.utils.DateUtil;
import frame.utils.RegularUtil;

public class PhoneUtil {

    public static List<Contact> getContacts(Context context) {
        ArrayList<Contact> contacts = new ArrayList<>();
        Cursor cursor = null;
        try {
            //生成ContentResolver对象
            ContentResolver contentResolver = context.getContentResolver();

            //搜索字段
            String[] projection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            // 获得所有的联系人
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
            if (cursor == null)
                return contacts;
            // 循环遍历
            if (cursor.moveToFirst()) {
                do {
                    // 获得联系人的ID
                    String contactId = cursor.getString(0);
                    // 获得联系人姓名
                    String displayName = cursor.getString(1);
                    //获取联系人的号码
                    String phoneNumber = cursor.getString(2);

                    //公司名称
                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{contactId,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = contentResolver.query(ContactsContract.Data.CONTENT_URI,null, orgWhere, orgWhereParams, null);
                    String companyName = null;
                    if (orgCur != null && orgCur.moveToFirst()) {
                        //组织名 (公司名字)
                        companyName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        orgCur.close();
                    }
                    if (!TextUtil.isStrNull(displayName) && !TextUtil.isStrNull(phoneNumber)) {
                        Contact contact = new Contact();
                        contact.setName(displayName);
                        contact.setPhone(phoneNumber);
                        contact.setCompanyName(companyName);
                        contacts.add(contact);
                    }
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return contacts;
    }

    public static List<Contact> getContactsList(Activity activity){
        List<Contact> data = null;
        try {
            data = new ArrayList<>();

            Map<Integer,List<Contact>> dataMap = new HashMap<>();

            if (activity == null) return data;
            ContentResolver resolver = activity.getContentResolver();
            //搜索字段
            String[] projection = new String[]{
                                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                                ContactsContract.Contacts.DISPLAY_NAME};
            // 获取手机联系人
            Cursor contactsCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);

            List<Integer> contactIdList = new ArrayList<>();

            if (contactsCursor != null) {
                while (contactsCursor.moveToNext()) {
                    //获取联系人的ID
                    int contactId = contactsCursor.getInt(0);
                    //获取联系人的姓名
                    String name = contactsCursor.getString(2);
                    //获取联系人的号码
                    String phoneNumber = contactsCursor.getString(1);

                    if (TextUtil.isEmpty(phoneNumber)) continue;

                    if (!contactIdList.contains(contactId)) {
                        contactIdList.add(contactId);
                    }
                    Contact contacts = new Contact();
                    contacts.setName(name);
                    contacts.setPhone(phoneNumber);

                    //如果联系人Map已经包含该contactId
                    if (dataMap.containsKey(contactId)) {
                        List<Contact> contacts1 = dataMap.get(contactId);
                        contacts1.add(contacts);
                    } else {
                        List<Contact> contactListT = new ArrayList<>();
                        contactListT.add(contacts);
                        dataMap.put(contactId,contactListT);
                    }
                }
                contactsCursor.close();
            }

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < contactIdList.size(); i++) {
                stringBuffer.append("'" + contactIdList.get(i) + "'");
                if (i != contactIdList.size()-1){
                    stringBuffer.append(",");
                }
            }
            String[] companyProjection = new String[]{
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Organization.DATA
            };

            //公司名称
            String orgWhere = ContactsContract.Data.CONTACT_ID + " in("+ stringBuffer.toString() + ") AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] orgWhereParams = new String[]{ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
            Cursor orgCur = resolver.query(ContactsContract.Data.CONTENT_URI,companyProjection, orgWhere, orgWhereParams, null);
            if (orgCur != null) {
                while (orgCur.moveToNext()) {
                    int contactId = orgCur.getInt(0);
                    //组织名 (公司名字)
                    String companyName = orgCur.getString(1);
                    if(dataMap.containsKey(contactId)){
                        if (companyName != null){
                            List<Contact> contacts = dataMap.get(contactId);
                            for (int i = 0; i < contacts.size(); i++) {
                                contacts.get(i).companyName = companyName;
                            }
                        }
                    }
                }
                orgCur.close();
            }

            Set<Map.Entry<Integer, List<Contact>>> entries = dataMap.entrySet();
            Iterator<Map.Entry<Integer,List<Contact>>> iterator =entries.iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, List<Contact>> next = iterator.next();
                List<Contact> tempList = next.getValue();
                data.addAll(tempList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }



    public static class Contact {
        private String name;
        private String phone;
        private String companyName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCompanyName() {
            return companyName;
        }
        public void setCompanyName(String companyName) {
            this.companyName = companyName == null?"":companyName;
        }
    }

    public static Map<String, String> getPhoneContactsInfo(Context context) {
        Map<String, String> contactsMap = null;
        try {
            contactsMap = new HashMap<String, String>();
            //生成ContentResolver对象
            ContentResolver contentResolver = context.getContentResolver();
            // 获得所有的联系人
        /*Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
         */
            //这段代码和上面代码是等价的，使用两种方式获得联系人的Uri
            Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/contacts"), null, null, null, null);
            // 循环遍历
            if (cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                int displayNameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                do {
                    // 获得联系人的ID
                    String contactId = cursor.getString(idColumn);
                    // 获得联系人姓名
                    String displayName = cursor.getString(displayNameColumn);
                    //显示获得的联系人信息
//                System.out.println("联系人姓名：" + displayName);
                    // 查看联系人有多少个号码，如果没有号码，返回0
                    int phoneCount = cursor.getInt(cursor
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (phoneCount > 0) {
                        // 获得联系人的电话号码列表
                        Cursor phoneCursor = context.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + "=" + contactId, null, null);
                        if (phoneCursor.moveToFirst()) {
                            do {
                                //遍历所有的联系人下面所有的电话号码
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                //显示获得的号码
//                            System.out.println("联系人电话："+phoneNumber);
                                if (!contactsMap.containsKey(phoneNumber) && phoneNumber != null) {
//                                    phoneNumber = handlePhoneNum(phoneNumber);
                                    if (!TextUtils.isEmpty(phoneNumber)) {
                                        contactsMap.put(phoneNumber, displayName);
                                    }
                                }
                            } while (phoneCursor.moveToNext());
                        }
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactsMap;
    }


    private static String handlePhoneNum(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum)) {
            phoneNum = phoneNum.replaceAll("\\D", "");
        }
        if (phoneNum.length() == 13 && phoneNum.startsWith("86")) {
            phoneNum = phoneNum.substring(2);
            if (RegularUtil.isPhone(phoneNum)) {
                return phoneNum;
            }
        } else if (phoneNum.length() == 11) {
            return RegularUtil.isPhone(phoneNum) ? phoneNum : null;
        }
        return null;
    }

    public static int judgePhoneLast4Valid(String phoneNumber) {
        String last4PhoneNum = phoneNumber.substring(7);
        int is4Same = 0;
        for (int i = 0; i < last4PhoneNum.length() - 1; i++) {
            char c = last4PhoneNum.charAt(i);
            if (c == last4PhoneNum.charAt(i + 1)) {
                is4Same = 1;
                continue;
            }
            is4Same = 0;
            break;
        }
        return is4Same;
    }


    public static int judgePhoneLast8Valid(String phoneNumber) {
        String[] reg = {"01234567", "12345678", "23456789"};
        if (phoneNumber.contains(reg[0]) || phoneNumber.contains(reg[1]) || phoneNumber.contains(reg[2])) {
            return 1;
        }
        return 0;
    }

    public static List<SmsInfo.SmsInfoBean> getSmsInfos2(Context context) {
        Cursor cursor = null;
        List<SmsInfo.SmsInfoBean> list = new ArrayList<>();
        List<String> personList = new ArrayList<>();
        try {
            final String SMS_URI_INBOX = "content://sms/";// 所有内容

            ContentResolver cr = context.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type", "protocol", "read", "service_center ", "status", "thread_id"};
            Uri uri = Uri.parse(SMS_URI_INBOX);
            cursor = cr.query(uri, projection, null, null, "date desc");
            if (cursor == null)
                return list;

            while (cursor.moveToNext()) {
                SmsInfo.SmsInfoBean smsInfoBean = new SmsInfo.SmsInfoBean();
                int idColumn = cursor.getColumnIndex("_id"); // 自增ID
                int nameColumn = cursor.getColumnIndex("person");// 联系人姓名列表序号，一般从1开始；陌生人为null，有些手机上是0
                int phoneNumberColumn = cursor.getColumnIndex("address");// 手机号
                int smsbodyColumn = cursor.getColumnIndex("body");// 短信内容
                int dateColumn = cursor.getColumnIndex("date");// 日期
                int typeColumn = cursor.getColumnIndex("type");  // 收发类型 1表示接收 2表示发送 ALL = 0;INBOX = 1;SENT = 2;DRAFT = 3;OUTBOX = 4;FAILED = 5; QUEUED = 6
                int protocolColumn = cursor.getColumnIndex("protocol"); // 0 SMS_RPOTO, 1 MMS_PROTO
                int serviceCenterColumn = cursor.getColumnIndex("service_center"); // 短信服务中心号码编号
                int statusColumn = cursor.getColumnIndex("status"); // 发送状态 -1 接收，0 complete, 64 pending, 128 failed
                int threadIdColumn = cursor.getColumnIndex("thread_id"); // 序号，同一发信人的id相同
                int readColumn = cursor.getColumnIndex("read"); // 0 :已读， 1 : 未读


                // 获取数据
                String nameId = cursor.getString(nameColumn) != null? cursor.getString(nameColumn) : "";
                String phoneNumber = cursor.getString(phoneNumberColumn) != null? cursor.getString(phoneNumberColumn) : "";
                String smsBody = cursor.getString(smsbodyColumn) != null? cursor.getString(smsbodyColumn) : "";
                Date d = new Date(Long.parseLong(cursor.getString(dateColumn)));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                String date = dateFormat.format(d);
                String id = cursor.getString(idColumn);
                String protocol = cursor.getString(protocolColumn) != null? cursor.getString(protocolColumn) : "";
                String serviceCenter = cursor.getString(serviceCenterColumn) != null? cursor.getString(serviceCenterColumn) : "";
                String status = cursor.getString(statusColumn);
                String threadId = cursor.getString(threadIdColumn);
                String read = cursor.getString(readColumn);
                int type = cursor.getInt(typeColumn);

                // 封装数据
                smsInfoBean.setBody(smsBody);
                smsInfoBean.setPerson(nameId);
                smsInfoBean.setAddress(phoneNumber);
                smsInfoBean.setDate(date);
                smsInfoBean.setId(id);
                smsInfoBean.setProtocol(protocol);
                smsInfoBean.setServiceCenter(serviceCenter);
                smsInfoBean.setStatus(status);
                smsInfoBean.setThreadId(threadId);
                smsInfoBean.setRead(read);
                smsInfoBean.setType(String.valueOf(type));
                /*if (type == 1) {
                    smsInfoBean.setType("1");
                } else {
                    smsInfoBean.setType("2");
                }*/
                list.add(smsInfoBean);
            }

            try {
                //Uri personUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, list.get(i).getAddress());
                cursor = cr.query(ContactsContract.Data.CONTENT_URI,
                        new String[]{
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
                //MmLogger.d(">>>>>>>>>>>>>>>>>>>>>>>>>去除null之前" + list.size());
                //list.removeAll(Collections.singleton(null));

                //MmLogger.d(">>>>>>>>>>>>>>>>>>>>>>>>>去除null之后" + list.size());
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getPerson() == null || list.get(i).getPerson().equals("")) {
//                        list.get(i).setPerson(list.get(i).getAddress());
                        continue;
                    }
                    //Cursor localCursor = null;

                    Uri personUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, list.get(i).getAddress());
                    cursor = cr.query(personUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID}, null, null, null);
                    if (cursor == null)
                        continue;
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                        list.get(i).setPerson(name);
                        //smsInfoBean.setAddress(phoneNumber);
                    }


                }

                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }

            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }


        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }


        return list;
    }


    @SuppressLint("MissingPermission")
    public static List<RecordEntity> getCallLog(Context context) {
        Cursor cursor = null;
        List<RecordEntity> recordList = null;
        try {

            recordList = new ArrayList<>();
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                RecordEntity record = new RecordEntity();
                String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                if (!TextUtil.isEmpty(name)) {
                    record.name = name;
                }
                record.phone = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                record.callTime = DateUtil.getDate(new Date(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))), DateUtil.Format.SECOND.getValue());
                record.sumDuration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));

                recordList.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return recordList;
    }

    public static class RecordEntity {
        /**
         * 姓名
         */
        public String name = "";
        /**
         * 手机号
         */
        public String phone;
        /**
         * 时间
         */
        public String callTime;
        /**
         * 通话时长
         **/
        public long sumDuration;

        @Override
        public String toString() {
            return "RecordEntity [toString()=" + name + "," + phone + "," + callTime + "," + sumDuration + "]";
        }
    }

}
