package com.nhson.chatservice.domain;

import java.util.List;

public record PAGResponse(List<String> pag,String lastTimeLoad) {
}
