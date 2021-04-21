package com.example.demo.service.impl;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ProtobufFormatter {
    private static Logger logger = LoggerFactory.getLogger(ProtobufFormatter.class);

    public static Map<String, Object> formatMessage(Message message) {
        if (null == message) {
            logger.debug("Message is null.");
            return null;
        }
        Map<Descriptors.FieldDescriptor, Object> fieldMap = message.getAllFields();

        Map<String, Object> map = new LinkedHashMap<>();

        Descriptors.FieldDescriptor field = null;
        String name = null;
        Descriptors.FieldDescriptor.JavaType type = null;
        for (Iterator<Descriptors.FieldDescriptor> keys = fieldMap.keySet().iterator(); keys.hasNext();) {
            field = keys.next();
            name = field.getName();
            type = field.getJavaType();
            if (type.equals(Descriptors.FieldDescriptor.JavaType.MESSAGE)) {
                Object obj = fieldMap.get(field);
                if (obj instanceof Message) {
                    map.put(name, formatMessage((Message) obj));
                } else {
                    @SuppressWarnings("unchecked")
                    List<Object> objList = (List<Object>) obj;
                    if (null != objList && !objList.isEmpty()) {
                        Object firstNodeObj = objList.get(0);
                        if (firstNodeObj instanceof Message) {
                            List<Map<String, Object>> mapList = new ArrayList<>();
                            for (Object nodeObj : objList) {
                                mapList.add(formatMessage((Message) nodeObj));
                            }
                            map.put(name, mapList);
                        } else {
                            map.put(name, objList);
                        }
                    }
                }
            } else if (type.equals(Descriptors.FieldDescriptor.JavaType.ENUM)) {
                Descriptors.EnumValueDescriptor en = (Descriptors.EnumValueDescriptor) fieldMap.get(field);
                map.put(name, en.getName());
            } else if (type.equals(Descriptors.FieldDescriptor.JavaType.BYTE_STRING)) {
                if (!field.isRepeated()) {
                    ByteString byteString = (ByteString) fieldMap.get(field);
                    map.put(name, byteString.toStringUtf8());
                } else {
                    Object obj = fieldMap.get(field);
                    @SuppressWarnings("unchecked")
                    List<Object> objList = (List<Object>) obj;
                    if (null != objList && !objList.isEmpty()) {
                        Object firstNodeObj = objList.get(0);
                        if (firstNodeObj instanceof Message) {
                            List<Map<String, Object>> mapList = new ArrayList<>();
                            for (Object nodeObj : objList) {
                                mapList.add(formatMessage((Message) nodeObj));
                            }
                            map.put(name, mapList);
                        } else if (type.equals(Descriptors.FieldDescriptor.JavaType.BYTE_STRING)) {
                            List<String> mapList = new ArrayList<>();
                            for (Object nodeObj : objList) {
                                ByteString byteString = (ByteString) nodeObj;
                                mapList.add(byteString.toStringUtf8());
                            }
                            map.put(name, mapList);
                        } else {
                            map.put(name, objList);
                        }
                    }
                }
            } else {
                map.put(name, fieldMap.get(field));
            }
        }
        return map;
    }
}
