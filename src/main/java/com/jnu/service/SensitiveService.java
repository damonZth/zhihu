package com.jnu.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Damon on 2017/8/15.
 */
@Service
public class SensitiveService implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private static final String DEFAULT_REPLACEMENT = "**";

    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try{
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineTxt;
            while((lineTxt = bufferedReader.readLine()) != null){
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            inputStreamReader.close();
        }catch (Exception e){
            logger.error("读取敏感词失败" + e.getMessage());
        }
    }

    /**
     * 过滤敏感词
     * @param text
     * @return
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return text;
        }
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder stringBuilder = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;
        while(position < text.length()){
            char c = text.charAt(position);
            if(isSymbol(c)){
                if(tempNode == rootNode){
                    stringBuilder.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                stringBuilder.append(text.charAt(begin));
                position = begin + 1;
                begin = position;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                stringBuilder.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            }else{
                ++position;
            }
        }
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
    }


    private void addWord(String lineText){
        TrieNode tempNode = rootNode;
        //循环每个字符
        for(int i = 0; i < lineText.length(); i++){
            Character character = lineText.charAt(i);
            if(isSymbol(character)){
                continue;
            }
            TrieNode node = tempNode.getSubNode(character);
            if(node == null){
                //初始化node节点
                node = new TrieNode();
                tempNode.addSubNode(character, node);
            }
            tempNode = node;
            if(i == lineText.length() - 1){
                //关键词遍历结束，设置结束标志
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 判断是否是一个字符
     * @param c
     * @return
     */
    private boolean isSymbol(char c){
        int ic = (int)c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic >0x9FFF);
    }

    private TrieNode rootNode = new TrieNode();


    //前缀树节点
    private class TrieNode{
        //是不是关键词的结尾
        private boolean end = false;
        //key下一个字符，value对应的节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        //指定位置，添加节点
        public void addSubNode(Character key, TrieNode node){
            subNodes.put(key, node);
        }
        //获取下一个节点
        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        public boolean isKeywordEnd(){
            return end;
        }

        public void setKeywordEnd(boolean end){
            this.end = end;
        }

        public int getSubNodeCount(){
            return subNodes.size();
        }
    }


}
