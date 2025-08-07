package com.github.sceneren.common.socket.const

object SocketConst {
    var HOST = "yj1.pigeonfan.com"
    const val TCP_PORT = 6170
    const val START = "agzsta"
    const val END = "agzend"
    const val CENTER = "dovezsj"
    const val VERSION = 1
    const val SOURCE = "1"
    const val LANGUAGE = 1 //1中文，2英文

    const val CONNECTION_AUTH = 1 //连接鉴权
    const val HEART = 2 //心跳
    const val HOME_DEVICE_STATUS = 3 //首页设备状态
    const val SCANS_DEVICE_STATUS = 4 //配网时检查设备是否在线/是否激活
    const val CONNECTION_RESULT_DEVICE_STATUS = 5 //配网时收到设备已连接服务器的在线状态
    const val BIND_SHED = 6 //配网时绑定设备mac到对应用户鸽棚
    const val DEVICE_UPGRADE_INFO = 7 //获取升级描述
    const val DEVICE_UPGRADE = 8 //提交版本更新
    const val DEVICE_UPGRADE_STATUS = 9 //获取更新状态
    const val DEVICE_DOING_STATUS = 100 //设备执行状态   喂食、喂水、放飞 公用

    //喂食
    const val FEED_DATA_OR_STATUS = 1002 //获取喂食基本数据及状态
    const val FEED_STATUS = 1003 //获取喂食状态
    const val FEED_GET_STRATEGY = 1004 //获取喂食策略数据
    const val FEED_POST_STRATEGY = 1005 //提交喂食策略数据
    const val FEED_OPEN = 1006 //打开手动喂食数据
    const val FEED_CLOSE = 1007 //关闭手动喂食数据
    const val FEED_VOICE_OPEN_CLOSE = 1008 //打开关闭声音
    const val FEED_LIGHT_OPEN_CLOSE = 1009 //打开关闭灯光
    const val STRATEGY_OPEN_CLOSE = 1010 //打开关闭自动策略   喂食、喂水、放飞 公用
    const val STRIP_FEED_GET_STRATEGY = 1011 //获取长条形喂食策略数据
    const val STRIP_FEED_POST_STRATEGY = 1012 //提交长条形喂食策略数据

    //喂水
    const val WATER_DATA_OR_STATUS = 2002 //获取喂水基本数据及状态
    const val WATER_STATUS = 2003 //获取喂水状态
    const val WATER_GET_STRATEGY = 2004 //获取喂水策略数据
    const val WATER_POST_STRATEGY = 2005 //提交喂水策略数据
    const val WATER_OPEN = 2006 //打开手动喂水数据
    const val WATER_CLOSE = 2007 //关闭手动喂水数据
    const val WATER_LIGHT_OPEN_CLOSE = 2008 //打开关闭灯光

    //放飞门
    const val FLY_DATA_OR_STATUS = 3002 //获取放飞基本数据及状态
    const val FLY_STATUS = 3003 //获取放飞状态
    const val FLY_GET_STRATEGY = 3004 //获取放飞策略数据
    const val FLY_POST_STRATEGY = 3005 //提交放飞策略数据
    const val FLY_OPEN = 3006 //打开手动放飞数据
    const val FLY_CLOSE = 3007 //关闭手动放飞数据
    const val FLY_VOICE_OPEN_CLOSE = 3008 //打开关闭声音
    const val FLY_AUTO_DOOR_OPEN = 3009 //打开闯门数据
    const val FLY_AUTO_DOOR_CLOSE = 3010 //关闭闯门数据

    //百叶窗
    const val SHUTTER_DATA_OR_STATUS = 4002 //获取百叶窗设备列表,设备状态
    const val SHUTTER_STATUS = 4003 //获取百叶窗状态
    const val GET_SHUTTER_STRATEGY = 4004 //获取百叶窗策略数据
    const val POST_SHUTTER_STRATEGY = 4005 //提交百叶窗策略设置数据
    const val SHUTTER_OPEN = 4006 //打开手动百叶窗指令
    const val SHUTTER_CLOSE = 4007 //关闭手动百叶窗指令
    const val SHUTTER_GET_POSITION = 4008 //获取百叶窗位置
    const val SHUTTER_STRATEGY_OPEN_CLOSE = 4009 //修改百叶窗策略总开关(开启/关闭)
    const val SHUTTER_SET_POSITION = 4010 //发送选择的百叶窗方向数据
}