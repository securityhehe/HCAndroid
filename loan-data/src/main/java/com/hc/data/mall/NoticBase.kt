package com.hc.data.mall

open class NoticBase constructor(var noticeTime:String, var noticeContent:String)

class Announcement(noticeTime: String, noticeContent: String) : NoticBase(noticeTime, noticeContent) {
    var updateTime: String? = null
    var value: String? = null

    fun handlerSwapValue() {
        this.noticeTime = updateTime!!
        this.noticeContent = value!!
    }
}
class OrderNotice(noticeTime: String, noticeContent: String) :
    NoticBase(noticeTime, noticeContent) {
    var orderNotice: String? = null
    var updateTime: String? = null

    fun handlerSwapValue() {
        this.noticeTime = updateTime!!
        this.noticeContent = orderNotice!!
    }
}

class NoticeEntity {
    var announcementList: MutableList<Announcement>? = null
    var orderNoticeList: MutableList<OrderNotice>? = null

}