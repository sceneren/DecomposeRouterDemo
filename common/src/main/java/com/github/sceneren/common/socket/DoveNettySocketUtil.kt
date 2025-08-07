//package com.github.sceneren.common.socket
//
//import com.github.sceneren.common.socket.const.SocketConst
//import com.agz.hardware.socket.request.AuthReqInfo
//import wiki.scene.socket.NettyTcpClient
//
//class DoveNettySocketUtil private constructor() {
//    private var tcpClient: NettyTcpClient? = null
//
//    companion object {
//        val instance: DoveNettySocketUtil by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            DoveNettySocketUtil()
//        }
//    }
//
//    fun getTcpClient(uid: String?): NettyTcpClient {
//        if (tcpClient == null) {
//            val clientBuilder = NettyTcpClient.Builder()
//                    .setHost(SocketConst.HOST) //设置服务端地址
//                    .setTcpPort(SocketConst.TCP_PORT) //设置服务端端口号
//                    .setMaxReconnectTimes(10) //设置最大重连次数
//                    .setReconnectIntervalTime(5) //设置重连间隔时间。单位：秒
//                    .setIndex(0) //设置客户端标识.(因为可能存在多个tcp连接)
//                    .setMaxPacketLong(4 * 1024)
//                    .setStartPacketSeparator(SocketConst.START)
//                    .setPacketSeparator(SocketConst.END)//用特殊字符，作为分隔符，解决粘包问题，默认是用换行符作为分隔符
//
//            if (uid.isNullOrEmpty()) {
//                clientBuilder.setSendHeartBeat(true)
//            } else {
//                val reqInfo = AuthReqInfo(uid)
//                val heartBeatData = SocketDataUtil.createSendData(reqInfo, "1", SocketConst.HEART)
//
//                clientBuilder.setSendHeartBeat(true) //设置是否发送心跳
//                        .setHeartBeatData(heartBeatData) //设置心跳数据，可以是String类型，也可以是byte[]，以后设置的为准
//                        .setHeartBeatInterval(10) //设置心跳间隔时间。单位：秒
//
//            }
//            tcpClient = clientBuilder.build()
//            return tcpClient!!
//        } else {
//            return tcpClient!!
//        }
//
//
//    }
//
//    fun closeSocket() {
//        tcpClient?.let {
//            if (it.connectStatus) {
//                it.disconnect()
//            }
//            tcpClient = null
//        }
//    }
//
//    private fun connectSocket() {
//        tcpClient?.let {
//            if (!it.connectStatus) {
//                it.connect()
//            }
//        }
//    }
//}