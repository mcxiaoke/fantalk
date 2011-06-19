package project.fantalk.command;

import project.fantalk.api.Utils;
import project.fantalk.api.digu.DiGuService;
import project.fantalk.api.douban.DoubanServiceOAuth;
import project.fantalk.api.fanfou.FanfouService;
import project.fantalk.api.nets.NetsServiceOAuth;
import project.fantalk.api.sina.SinaServiceOAuth;
import project.fantalk.api.sohu.SohuServiceOAuth;
import project.fantalk.api.tencent.TencentServiceOAuth;
import project.fantalk.api.twitter.TwitterService;
import project.fantalk.model.Datastore;
import project.fantalk.model.Member;
import project.fantalk.xmpp.Message;
import project.fantalk.xmpp.XMPPUtils;

import com.google.appengine.api.xmpp.JID;


public class StatusHandler extends BaseCommand {

    public StatusHandler() {
        super("status", "me");
    }

    @Override
    public void doCommand(Message message, String argument) {
        JID sender = message.sender;
        String email = message.email;
        Member m = (Member) Datastore.getInstance().getAndCacheMember(email);
        StringBuilder sb = new StringBuilder();
        String pushType = m.getPushType();
        String pushName;
        if (pushType.equalsIgnoreCase("home")) {
            pushName = "主页消息推送开启";
        } else if (pushType.equalsIgnoreCase("mentions")) {
            pushName = "@消息推送开启";
        } else {
            pushName = "消息推送已关闭";
        }
        sb.append("你的帐号: ").append(m.getEmail()).append("\n").append(
                "消息同步: " + (m.isSyncOn() ? "开启" : "关闭")).append("\n").append(
                "消息推送: " + pushName).append("\n");
        // .append("Binding Account: ");
        StringBuilder bindingOkSb = new StringBuilder();
//        StringBuilder bindingErrorSb = new StringBuilder();
        if (!Utils.isEmpty(m.getPassword())) {
            bindingOkSb.append("饭否  ");
            // FanfouService fanfou = new FanfouService(m);
            // if (!fanfou.verify().isOk()) {
            // bindingErrorSb.append("饭否 ");
            // } else {
            // bindingOkSb.append("饭否 ");
            // }
        }
        if (!Utils.isEmpty(m.getTwitterPassword())) {
            bindingOkSb.append("推特  ");
            // TwitterService ts = new TwitterService(m);
            // if (!ts.verify().isOk()) {
            // bindingErrorSb.append("推特 ");
            // } else {
            // bindingOkSb.append("推特 ");
            // }
        }
        if (!Utils.isEmpty(m.getSinaPassword())) {
            bindingOkSb.append("新浪微博  ");
            // SinaServiceOAuth sinaOAuth = new SinaServiceOAuth(m);
            // if (!sinaOAuth.verify().isOk()) {
            // bindingErrorSb.append("新浪微博 ");
            // } else {
            // bindingOkSb.append("新浪微博 ");
            // }
        }
        if (!Utils.isEmpty(m.getTecentPassword())) {
            bindingOkSb.append("腾讯微博  ");
            // TencentServiceOAuth qq = new TencentServiceOAuth(m);
            // if (!qq.verify().isOk()) {
            // bindingErrorSb.append("腾讯微博 ");
            // } else {
            // bindingOkSb.append("腾讯微博 ");
            // }
        }
        if (!Utils.isEmpty(m.getSohuPassword())) {
//        	SohuServiceOAuth sohu = new SohuServiceOAuth(m);
//			if (sohu.verify().isOk()) {
//				bindingOkSb.append("搜狐微博  ");
//			}
        	bindingOkSb.append("搜狐微博  ");
        }
        if (!Utils.isEmpty(m.getNeteasePassword())) {
            bindingOkSb.append("网易微博  ");
            // NetsServiceOAuth nets = new NetsServiceOAuth(m);
            // if (!nets.verify().isOk()) {
            // bindingErrorSb.append("网易微博 ");
            // } else {
            // bindingOkSb.append("网易微博 ");
            // }
        }
        if (!Utils.isEmpty(m.getDoubanPassword())) {
            bindingOkSb.append("豆瓣我说 ");
            // DoubanServiceOAuth douban = new DoubanServiceOAuth(m);
            // if (!douban.verify().isOk()) {
            // bindingErrorSb.append("豆瓣 ");
            // } else {
            // bindingOkSb.append("豆瓣 ");
            // }
        }
        if (!Utils.isEmpty(m.getDiguPassword())) {
            bindingOkSb.append("嘀咕  ");
            // DiGuService digu = new DiGuService(m);
            // if (!digu.verify().isOk()) {
            // bindingErrorSb.append("嘀咕 ");
            // } else {
            // bindingOkSb.append("嘀咕 ");
            // }
        }
        if (Utils.isEmpty(bindingOkSb.toString())) {
            sb.append("未绑定任何账号\n");
        } else {
            // if (!Utils.isEmpty(bindingOkSb.toString())) {
            sb.append("绑定的账号：").append(bindingOkSb).append("\n");
            // }
            // if (!Utils.isEmpty(bindingErrorSb.toString())) {
            // sb.append("绑定失败的账号：").append(bindingErrorSb).append("\n");
            // }
        }
        XMPPUtils.sendMessage(sb.toString(), sender);

    }

    public String documentation() {
        return "-status\n查看个人的设置和状态信息，也可使用-me";
    }

}
