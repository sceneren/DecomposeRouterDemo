//package com.agz.hardware.socket.util
//
//import com.agz.hardware.socket.const.SocketConst
//import com.agz.hardware.socket.request.BaseReqInfo
//import com.blankj.utilcode.util.GsonUtils
//import com.blankj.utilcode.util.LogUtils
//
//object SocketDataUtil {
//    fun <T> createSendData(bodyInfo: T, reqId: String, reqType: Int): String {
//        val reqInfo: BaseReqInfo<T> = BaseReqInfo(reqId, reqType, SocketConst.SOURCE, SocketConst.SOURCE, SocketConst.VERSION, SocketConst.LANGUAGE, bodyInfo)
//        val jsonStr = GsonUtils.toJson(reqInfo)
//        LogUtils.e("请求的数据:$jsonStr")
//        return jsonStr
//    }
//
//    //请求码
//    fun getReqId(reqType: Int): String {
//        return "${reqType}_${System.currentTimeMillis()}"
//    }
//
//    fun getDeviceApConfigNetMsg(userId: String, name: String, pw: String, sheId: String): String {
//        val nameLength: String = if (name.toByteArray().size < 10) {
//            "0" + name.toByteArray().size
//        } else {
//            name.toByteArray().size.toString() + ""
//        }
//        val pwdLength: String = if (pw.toByteArray().size < 10) {
//            "0" + pw.toByteArray().size
//        } else {
//            pw.toByteArray().size.toString() + ""
//        }
//        val length = ("@" + userId + "N" + nameLength + name + "P"
//                + pwdLength + pw + "a08" + sheId + "%").toByteArray().size
//        val totalLength: String
//        val i = length.toString().toByteArray().size + length
//        totalLength = if (i < 10) {
//            "0$i"
//        } else {
//            i.toString() + ""
//        }
//        return "@" + userId + totalLength + "N" + nameLength + name + "P" + pwdLength + pw + "a08" + sheId + "%"
//    }
//
//}