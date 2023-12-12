package cn.xeblog.plugin.game.dld;

/**
 * @author eleven
 * @date 2023/10/12 15:54
 * @apiNote
 */
public class Const {
    /**
     * 错误编码
     */
    public static final Integer ERROR_CODE = 500;
    /**
     * 每行展示数字
     */
    public static final Integer COLUMN_NUM = 18;

    public static final String CLEAR_MSG = "青山湿遍君不知，一汪清水独自流";

    public static final String GAME_NAME = "爱坤大乐斗";

    public static final String INSTANCE_OVER_MSG = "当前副本已完成，请重新选择副本";

    /**
     * 基础url
     */
    public static String REMOTE_URL = "http://103.153.101.174:9528/ikun";
    public static String LOCAL_URL = "http://localhost:9528/ikun";
    public static String BASE_URL = REMOTE_URL;
    /**#pla
     * 系统控制器
     */
    public static String SYS_CONTROLLER = BASE_URL + "/sys";
    /**
     * 玩家控制器
     */
    public static String PLAYER_CONTROLLER = BASE_URL + "/player";
    /**
     * 战斗控制器
     */
    public static String BATTLE_CONTROLLER = BASE_URL + "/battle";
    /**
     * 副本控制器
     */
    public static String INSTANCE_CONTROLLER = BASE_URL + "/instance";
    /**
     * npc控制器
     */
    public static String INSTANCE_NPC_CONTROLLER = BASE_URL + "/instanceNpc";
    /**
     * 玩家武器
     */
    public static String PLAYER_WEAPON_CONTROLLER = BASE_URL + "/playerWeapon";
    /**
     * 登录
     */
    public static String SYS_LOGIN = SYS_CONTROLLER + "/login";
    /**
     * 注册接口
     */
    public static String SYS_REG = SYS_CONTROLLER + "/register";
    /**
     * 获取所有玩家
     */
    public static String PLAYER_GET_ALL = PLAYER_CONTROLLER + "/getAll";
    /**
     * 玩家信息
     */
    public static String PLAYER_DETAIL = PLAYER_CONTROLLER + "/detail";
    /**
     * 战斗
     */
    public static String BATTLE_DO = BATTLE_CONTROLLER + "/doBattle";
    public static String BATTLE_TAUNT = BATTLE_CONTROLLER + "/taunt";
    /**
     * 副本列表
     */
    public static String INSTANCE_LIST = INSTANCE_CONTROLLER + "/list";

    /**
     * 进入副本
     */
    public static String INSTANCE_JOIN = INSTANCE_CONTROLLER + "/join";
    public static String NPC_CHALLENGE = INSTANCE_NPC_CONTROLLER + "/challenge";
    /**
     * 获取玩家所有的武器
     */
    public static String GET_ALL_WEAPON = PLAYER_WEAPON_CONTROLLER + "/getAll";

}
