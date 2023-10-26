package cn.xeblog.plugin.game.dld;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.dld.model.Result;
import cn.xeblog.plugin.game.dld.model.common.Page;
import cn.xeblog.plugin.game.dld.model.dto.BattleDto;
import cn.xeblog.plugin.game.dld.model.dto.LoginDto;
import cn.xeblog.plugin.game.dld.model.dto.PlayerDto;
import cn.xeblog.plugin.game.dld.model.vo.PlayerInfoVo;
import cn.xeblog.plugin.game.dld.model.vo.PlayerVo;
import cn.xeblog.plugin.game.dld.model.vo.ProcessVo;
import cn.xeblog.plugin.game.dld.ui.IKunUi;
import cn.xeblog.plugin.game.dld.ui.game.MasterGame;
import cn.xeblog.plugin.game.dld.ui.game.PlayerInfoTab;
import cn.xeblog.plugin.game.dld.ui.game.PvpTab;
import cn.xeblog.plugin.game.dld.ui.login.LoginFormUi;
import cn.xeblog.plugin.game.dld.ui.login.MacLogin;
import cn.xeblog.plugin.game.dld.utils.HttpSendUtil;
import cn.xeblog.plugin.game.dld.utils.ResultUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import cn.xeblog.plugin.util.NotifyUtils;
import com.google.gson.Gson;
import com.intellij.ui.components.panels.VerticalBox;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static cn.xeblog.plugin.game.dld.Const.GAME_NAME;

/**
 * @author eleven
 * @date 2023/10/24 12:27
 * @apiNote
 */
@DoGame(Game.IKUN)
@Slf4j
public class IKunDldXl extends AbstractGame {

    /**
     * 主面板
     */
    private JPanel mainPanel;

    private JPanel centerPanel;

    private JPanel footerPanel;

    private JButton loginBtn;

    private Integer loginType = 0;
    /**
     * 切换用的tab页
     */
    private JTabbedPane tab;

    private VerticalBox box;

    private JTextArea fightArea;

    private JPanel playerArea;

    //=====以下为各种UI=====
    private IKunUi iKunUi;

    private LoginFormUi loginFormUi;

    private MacLogin macLogin;

    private MasterGame masterGame;

    private PvpTab pvpTab;

    private PlayerInfoTab playerInfoTab;


    private Gson gson = new Gson();

    @Override
    protected void start() {
        log.info("开始了");
        initLoginForm();
        centerPanel.removeAll();
        box = new VerticalBox();
        box.setMaximumSize(new Dimension(400, 300));
        box.add(loginFormUi.getLoginPanel());
        centerPanel.add(box);
        centerPanel.updateUI();
        log.info("结束了");
    }


    @Override
    protected void init() {
        log.info("当前初始化游戏面板");
        iKunUi = new IKunUi(GAME_NAME);
        if (mainPanel == null) {
            mainPanel = iKunUi.getMainPanel();
            centerPanel = iKunUi.getCenterPanel();
            footerPanel = iKunUi.getFooterPanel();
        }
        mainPanel.setMinimumSize(new Dimension(150, 200));
        footerPanel.add(getStartGameButton());
        footerPanel.add(getExitButton());
        mainPanel.updateUI();
        log.info("初始化完成");
    }

    @Override
    protected JPanel getComponent() {
        if (iKunUi == null) {
            iKunUi = new IKunUi(GAME_NAME);
        }
        return mainPanel;
    }

    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> start());
        return button;
    }

    private JButton getBackGameButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    /**
     * 初始化登录
     */
    private void initLoginForm() {
        loginFormUi = new LoginFormUi();
        JComboBox<String> typeComboBox = loginFormUi.getTypeComboBox();
        JPanel typePanel = loginFormUi.getTypePanel();
        typeComboBox.addItem("MAC自动登录");
        typeComboBox.addItem("账号密码登录");
        typeComboBox.addItem("邮箱验证码登录");
        initMacLogin();
        typePanel.add(macLogin.getMacPanel());
        typeComboBox.addActionListener(e -> {
            loginType = typeComboBox.getSelectedIndex();
            log.info("当前选择登录类型 - {}", loginType);
            typePanel.removeAll();
            switch (loginType) {
                case 0:
                    typePanel.add(macLogin.getMacPanel());
                    break;
                case 1:
                case 2:
                default:
                    break;
            }
            typePanel.updateUI();
            log.info("登录类型UI刷新成功");
        });
        loadLoginAction();
    }

    private void initMacLogin() {
        macLogin = new MacLogin();
        loginBtn = macLogin.getLoginBtn();
    }

    /**
     * 加载登录action
     */
    private void loadLoginAction() {
        log.info("加载登录按钮事件");
        loginBtn.addActionListener(e -> {
            switch (loginType) {
                case 0:
                    loginByMac();
                    break;
                case 1:
                    loginByAccount();
                    break;
                case 2:
                    loginByEmail();
                    break;
                default:
                    AlertMessagesUtil.showInfoDialog(GAME_NAME, "请选择MAC登录");
                    break;
            }
        });
    }

    /**
     * 初始化主游戏Tab面板
     */
    private void initMasterGame() {
        log.info("加载登录按钮事件完成");
        log.info("开始加载游戏主面板");
        masterGame = new MasterGame();
        tab = masterGame.getTab();
        loadPvpTab();
        JPanel gamePanel = masterGame.getGamePanel();
        Dimension maximumSize = new Dimension(400, 300);
        tab.setPreferredSize(maximumSize);
        log.info("GameTabbed width {} ， height {}", tab.getWidth(), tab.getHeight());
        refreshCenterPanel(gamePanel);
        log.info("游戏面板加载完成");
        tab.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tab.getSelectedIndex();
                switch (selectedIndex) {
                    case 0:
                        loadPvpTab();
                        break;
                    case 1:
                        loadInstanceTab();
                        break;
                    case 2:
                        loadPlayerInfoTab();
                        break;
                    case 3:
                        loadPackageTab();
                        break;
                    case 4:
                        loadBossTab();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * pvp Tab页
     */
    private void loadPvpTab() {
        tab.setSelectedIndex(0);
        JPanel pvpPanel = masterGame.getPvpPanel();
        pvpTab = new PvpTab();
        playerArea = pvpTab.getPlayerArea();
        fightArea = pvpTab.getFightArea();
        JButton clearBtn = pvpTab.getClearBtn();
        clearBtn.addActionListener(e -> {
            fightArea.setText(Const.CLEAR_MSG);
            updateUI(tab, box, centerPanel);
        });
        JButton refreshBtn = pvpTab.getRefreshBtn();
        refreshPlayerList();
        refreshBtn.addActionListener(e -> invoke(this::refreshPlayerList));
        pvpPanel.add(pvpTab.getPvpPanel());
        tab.updateUI();
    }

    /**
     * 副本tab
     */
    private void loadInstanceTab() {

    }

    /**
     * 玩家信息
     */
    private void loadPlayerInfoTab() {
        tab.setSelectedIndex(2);
        JPanel playerPanel = masterGame.getPlayerPanel();
        playerPanel.removeAll();
        playerInfoTab = new PlayerInfoTab();
        JLabel lvLabel = playerInfoTab.getLvLabel();
        JProgressBar lvProgress = playerInfoTab.getLvProgress();
        JScrollPane attrScroll = playerInfoTab.getAttrScroll();
        invoke(() -> {
            Result result = HttpSendUtil.post(Const.PLAYER_DETAIL, new PlayerDto());
            if (Const.ERROR_CODE.equals(result.getCode())) {
                NotifyUtils.error(GAME_NAME, result.toString(), false);
            } else {
                PlayerInfoVo playerInfoVo = ResultUtil.convertObjData(result, PlayerInfoVo.class);
                String lvTips = String.format("%d/%d", playerInfoVo.getExp(), playerInfoVo.getNextLvExp());
                lvLabel.setText(String.format("Lv:%d(%s)", playerInfoVo.getLevel(), lvTips));
                lvProgress.setMaximum(playerInfoVo.getNextLvExp());
                lvProgress.setValue(playerInfoVo.getExp());

            }
        });
        playerPanel.add(playerInfoTab.getPlayerPanel(), BorderLayout.EAST);
        updateUI(tab);
    }

    /**
     * 背包
     */
    private void loadPackageTab() {

    }

    /**
     * boss
     */
    private void loadBossTab() {

    }

    private void updateUI(JComponent... panels){
        Arrays.asList(panels).forEach(JComponent::updateUI);
    }

    private void refreshPlayerList() {
        PlayerDto dto = new PlayerDto(new Page<PlayerVo>());
        Result result = HttpSendUtil.post(Const.PLAYER_GET_ALL, dto);
        Object data = result.getData();
        if (Const.ERROR_CODE.equals(result.getCode())) {
            fightArea.setText(result.toString());
        } else {
            // 防止出现战斗后列表紊乱的问题
            playerArea.removeAll();
            Page<PlayerVo> playerPage = ResultUtil.convertPageData(data, PlayerVo.class);
            List<PlayerVo> records = playerPage.getRecords();
            JPanel playerPanel = null;
            JButton battleBtn = null;
            JLabel playerInfoLabel = null;
            User currentUser = DataCache.getCurrentUser();
            int size = records.size();
            log.info("当前人数{}", size);
            playerArea.setLayout(new GridLayout(size, 1));
            long onlineCount = records.stream()
                    .filter(PlayerVo::getOnline)
                    .count();
            pvpTab.getPlayerLabel().setText(String.format("玩家列表(%d/%d)", onlineCount, size));
            for (PlayerVo record : records) {
                playerPanel = new JPanel();
                playerInfoLabel = new JLabel(String.format("%d [%s][Lv %d][%s]",
                        records.indexOf(record) + 1,
                        record.getRegion(),
                        record.getLevel(),
                        record.getNickname()
                ));
                playerInfoLabel.setForeground(record.getOnline() ? Color.GREEN : Color.GRAY);
                playerInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
                playerPanel.add(playerInfoLabel, BorderLayout.CENTER);

                if (!StrUtil.equalsIgnoreCase(record.getMac(), currentUser.getUuid())) {
                    battleBtn = new JButton("战斗");
                    playerPanel.add(battleBtn, BorderLayout.WEST);
                    battleBtn.addActionListener(e -> {
                        fightArea.setText(Const.CLEAR_MSG);
                        invoke(() -> {
                            Result processResult = HttpSendUtil.post(Const.BATTLE_DO, new BattleDto(currentUser.getUuid(), record.getMac()));
                            List<ProcessVo> list = ResultUtil.convertListData(processResult, ProcessVo.class);
                            list.forEach(System.out::println);
                            list.forEach(item -> {
                                fightArea.append(String.format("%s \n", item.getProcess()));
                            });
                            refreshPlayerList();
                        });
                    });
                }
                playerArea.add(playerPanel, BorderLayout.WEST);
            }
            updateUI(playerArea, tab, box, centerPanel);
        }
    }

    /**
     * 刷新centerPanel
     *
     * @param panel
     */
    private void refreshCenterPanel(JPanel panel) {
        centerPanel.removeAll();
        box.removeAll();
        box.add(panel);
        centerPanel.add(box);
        centerPanel.updateUI();
    }

    private void loginByMac() {
        User currentUser = DataCache.getCurrentUser();
        if (currentUser == null || StrUtil.isBlank(currentUser.getUuid())) {
            AlertMessagesUtil.showErrorDialog(GAME_NAME, "请先登录插件再进行游戏");
            // TODO: 2023/10/24 自动执行 #over 9
            return;
        } else {
            LoginDto macLoginDto = LoginDto.macLogin();
            invoke(() -> {
                log.info("当前开始执行登录流程 地址 {}", Const.SYS_LOGIN);
                Result loginResult = HttpSendUtil.post(Const.SYS_LOGIN, macLoginDto);
                log.info("当前登录返回结果 -{}", loginResult);
                if (loginResult.getCode() == 200) {
                    DataCache.loginToken = String.format("Bearer %s", loginResult.getData().toString());
                    initMasterGame();
                } else {
                    AlertMessagesUtil.showErrorDialog(GAME_NAME, loginResult.getMessage());
                }
            });
        }
    }

    private void loginByAccount() {
        AlertMessagesUtil.showInfoDialog(GAME_NAME, "功能暂未开发，请选择MAC登录");
    }

    private void loginByEmail() {
        AlertMessagesUtil.showInfoDialog(GAME_NAME, "功能暂未开发，请选择MAC登录");
    }
}
