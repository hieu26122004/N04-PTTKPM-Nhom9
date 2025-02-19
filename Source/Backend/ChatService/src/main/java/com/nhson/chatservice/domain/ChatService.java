package com.nhson.chatservice.domain;

import com.nhson.chatservice.repository.PrivateChatMessageRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ChatService {
    private static final Log log = LogFactory.getLog(ChatService.class);
    private final int timeWindow = 3;
    private PrivateChatMessageRepository privateChatMessageRepository;

    public ChatService(PrivateChatMessageRepository privateChatMessageRepository) {
        this.privateChatMessageRepository = privateChatMessageRepository;
    }

    public PAGResponse findRecentChatParticipantsAndGroups(JwtAuthenticationToken authenticationToken, Date lastTimeLoad){

        log.info(">>>>>>>>>>>>>>> BẮT ĐẦU LOAD NGƯỜI DÙNG VÀ NHÓM <<<<<<<<<<<<<");
        if(lastTimeLoad.after(new Date())){
            throw new IllegalArgumentException("The provided time for loading cannot in the future");
        }
        String username = (String) authenticationToken.getToken().getClaims().get("username");

        Calendar cal = Calendar.getInstance();
        cal.setTime(lastTimeLoad);
        cal.add(Calendar.DAY_OF_YEAR, timeWindow);
        Date endDate = cal.getTime();

        List<String> response = privateChatMessageRepository.findRecentChatParticipantsAndGroups(username,lastTimeLoad,endDate);
        log.info(">>>>>>>>>>>>>>> TÌM THẤY " + response.size() + " nhóm và người dùng <<<<<<<<<<<<<<<");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate =  formatter.format(endDate);

        return new PAGResponse(response,formattedDate);
    }

    public List<PrivateChatMessage> findRecentMessages(JwtAuthenticationToken authenticationToken, Date lastTimeLoad,String targetUsername){

        log.info(">>>>>>>>>>>>>>> BẮT ĐẦU LOAD TIN NHẮN GẦN NHẤT <<<<<<<<<<<<<");
        if(lastTimeLoad.after(new Date())){
            throw new IllegalArgumentException("The provided time for loading cannot in the future");
        }
        String username = (String) authenticationToken.getToken().getClaims().get("username");
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastTimeLoad);
        cal.add(Calendar.DAY_OF_YEAR, timeWindow);
        Date endDate = cal.getTime();

        List<PrivateChatMessage> response = privateChatMessageRepository.findRecentMessages(username,lastTimeLoad,endDate,targetUsername);
        log.info(">>>>>>>>>>>>>>> TÌM THẤY " + response.size() + " TIN NHẮN <<<<<<<<<<<<<<<");
        return response;
    }
}
