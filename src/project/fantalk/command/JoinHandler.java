package project.fantalk.command;

import java.util.logging.Logger;

import project.fantalk.api.Utils;
import project.fantalk.model.Datastore;
import project.fantalk.model.Member;
import project.fantalk.xmpp.Message;
import project.fantalk.xmpp.XMPPUtils;


import com.google.appengine.api.xmpp.JID;

public class JoinHandler implements CommandHandler {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(JoinHandler.class
            .getName());

    public JoinHandler() {}

    public void doCommand(Message message) {
        JID sender = message.sender;
        StringBuilder sb = new StringBuilder();
        sb.append("=== 欢迎使用饭否GTalk机器人 FanTalk ===\n").append(
                "发布消息前请先使用-bind fanfou username password 绑定饭否帐号\n").append(
                "绑定成功后就可以直接发送消息更新饭否了\n").append(
                "项目主页: http://code.google.com/p/fanfoutalk/\n").append(
                "FanTalk主页：http://"+Utils.getAppID()+".appspot.com/\n");
        XMPPUtils.sendMessage(sb.toString(), sender);
    }

    public String documentation() {
        return null;
    }

    public boolean matches(Message message) {
        Datastore datastore = Datastore.getInstance();
        String email = message.email;
        Member m = (Member) datastore.getAndCacheMember(email);
        if (m.isNewer()) {
            m.setNewer(false);
            m.put();
            return true;
        } else {
            return false;
        }
    }

}
