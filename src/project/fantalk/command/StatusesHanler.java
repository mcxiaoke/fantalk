package project.fantalk.command;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import project.fantalk.api.Utils;
import project.fantalk.api.fanfou.FanfouService;
import project.fantalk.api.fanfou.Status;
import project.fantalk.model.Datastore;
import project.fantalk.model.Member;
import project.fantalk.xmpp.Message;
import project.fantalk.xmpp.XMPPUtils;


import com.google.appengine.api.xmpp.JID;

public abstract class StatusesHanler extends BaseCommand {

    StatusesHanler(String name, String... otherNames) {
		super(name, otherNames);
	}
    
	public abstract List<Status> getStatuses(FanfouService fanfou, int count,
			String lastId, String maxId, int page);

	public abstract String getMessage();
	
	public String getLastId(Member m) {
		return null;
	}
	
	public void updateMember(Member m, String lastId) {
	}
	
	@Override
    public void doCommand(Message message, String argument) {
        int count = 5;
        if (!Utils.isEmpty(argument)) {
            count = Utils.toInt(argument);
        }
        JID sender = message.sender;
        String email = message.email;
        Datastore datastore = Datastore.getInstance();
        Member m = (Member) datastore.getAndCacheMember(email);
        if (!Utils.canDo(m.getLastActive())) {
				XMPPUtils.sendMessage("操作过于频繁，请10秒后再试！", sender);
				return;
        }
        if (!Utils.isEmpty(m.getUsername())) {
            m.setLastActive(Calendar.getInstance(
                    TimeZone.getTimeZone("GMT+08:00")).getTime());// 更新最后一次API活动时间
            m.put();
			String messages = getAndProcessStatus(count, m);
			if (Utils.isEmpty(messages)) {
				XMPPUtils.sendMessage(getMessage(), sender);
			} else {
				XMPPUtils.sendMessage(messages, sender);
			}
        } else {
            XMPPUtils.sendMessage("你还未绑定饭否帐号，无法查看消息！", sender);
        }
    }

	public String getAndProcessStatus(int count, Member m) {
		String lastId = getLastId(m);
		FanfouService fs = new FanfouService(m);
		List<Status> statuses = getStatuses(fs, count, lastId, null, 0);
		if (statuses == null || statuses.isEmpty()) {// 如果无未读消息
			return null;
		} else {
			StringBuilder sb = processStatuses(m, statuses);
			String messages = sb.toString();
			if (Utils.isEmpty(messages)) {
				return null;
			} else {
				return messages;
			}
		}
	}

	private StringBuilder processStatuses(Member m, List<Status> statuses) {
		StringBuilder sb = new StringBuilder();
		Map<Integer, String> map = new HashMap<Integer, String>();
		String username = m.getUsername();
		for (int i = 0; i < statuses.size(); i++) {
		    Status s = statuses.get(i);
		    String userId = s.getUserId();
			if (i == 0) {
				updateMember(m, s.getId());
			}
		    if (userId.equalsIgnoreCase(username)) {
		        continue;// 不保存和推送自己发送的消息
		    }
		    String name = s.getUserName();
			String text = s.getText().replaceAll("<b>", "")
					.replaceAll("</b>", "").replaceAll("&quot;", "'");
//		    String id = s.getId();
		    String time = Utils.getInterval(s.getCreatedAt());
		    map.put(i, s.process());
		    sb.append(name).append("：").append(text).append("  [ ID:")
		            .append(i)
		            /** .append(id) */
		            .append(",  Time:").append(time).append(" ]\n\n");
		}
		if (map!= null && !map.isEmpty()) {
			Datastore.getInstance().addStatusesCache(m.getEmail(), map);
		}
		if(!Utils.isEmpty(sb.toString())){
		    sb.append("------------------------------\n");
		}
		return sb;
	}
}
