package com.infina.hissenet.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.infina.hissenet.common.ApiResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final String MASK = "****";
    private static final String UNLOGGABLE = "[UNLOGGABLE]";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS);
    }

    private static final List<String> SENSITIVE_FIELD_NAMES = Arrays.asList(
            "password", "newpassword", "confirmnewpassword", "passwordhash"
    );

    @Around("execution(* com.infina.hissenet.controller..*(..)) || execution(* com.infina.hissenet.service..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        boolean isController = className.contains(".controller.");

        Object[] args = joinPoint.getArgs();
        Object[] maskedArgs = maskSensitiveData(args);

        Map<String, Object> enterLog = new HashMap<>();
        enterLog.put("event", "ENTER");
        enterLog.put("timestamp", LocalDateTime.now().toString());
        enterLog.put("className", className);
        enterLog.put("methodName", methodName);
        enterLog.put("args", maskedArgs);
        enterLog.put("type", isController ? "CONTROLLER" : "SERVICE");

        logger.info("{}", toJsonString(enterLog));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            Map<String, Object> exceptionLog = new HashMap<>();
            exceptionLog.put("event", "EXCEPTION");
            exceptionLog.put("timestamp", LocalDateTime.now().toString());
            exceptionLog.put("className", className);
            exceptionLog.put("methodName", methodName);
            exceptionLog.put("errorMessage", ex.getMessage());
            exceptionLog.put("errorType", ex.getClass().getSimpleName());
            exceptionLog.put("type", isController ? "CONTROLLER" : "SERVICE");

            logger.error("{}", toJsonString(exceptionLog));
            throw ex;
        }

        long duration = System.currentTimeMillis() - startTime;

        if (isController) {
            logControllerResult(className, methodName, result, duration);
        } else {
            logServiceResult(className, methodName, result, duration);
        }

        return result;
    }

    private void logControllerResult(String className, String methodName, Object result, long duration) {
        Map<String, Object> exitLog = new HashMap<>();
        exitLog.put("event", "EXIT");
        exitLog.put("timestamp", LocalDateTime.now().toString());
        exitLog.put("className", className);
        exitLog.put("methodName", methodName);
        exitLog.put("duration", duration);
        exitLog.put("type", "CONTROLLER");

        if (result instanceof ApiResponse) {
            ApiResponse<?> apiResponse = (ApiResponse<?>) result;
            exitLog.put("responseType", "ApiResponse");
            exitLog.put("status", apiResponse.getStatus());
            exitLog.put("message", apiResponse.getMessage());
            exitLog.put("data", safeSerializeObject(apiResponse.getData()));
        } else {
            exitLog.put("responseType", result != null ? result.getClass().getSimpleName() : "void");
            exitLog.put("result", safeSerializeObject(result));
        }

        logger.info("{}", toJsonString(exitLog));
    }

    private void logServiceResult(String className, String methodName, Object result, long duration) {
        Map<String, Object> exitLog = new HashMap<>();
        exitLog.put("event", "EXIT");
        exitLog.put("timestamp", LocalDateTime.now().toString());
        exitLog.put("className", className);
        exitLog.put("methodName", methodName);
        exitLog.put("duration", duration);
        exitLog.put("type", "SERVICE");

        if (result != null) {
            exitLog.put("returnType", result.getClass().getSimpleName());
            exitLog.put("returnValue", safeSerializeObject(result));
        } else {
            exitLog.put("returnType", "void");
        }

        logger.info("{}", toJsonString(exitLog));
    }

    private Object safeSerializeObject(Object obj) {
        if (obj == null) {
            return null;
        }

        if (isEntity(obj)) {
            try {
                Field idField = obj.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                Object id = idField.get(obj);
                return obj.getClass().getSimpleName() + "[id=" + id + "]";
            } catch (Exception e) {
                return obj.getClass().getSimpleName() + "[UNSERIALIZABLE]";
            }
        }

        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return obj;
        }

        if (obj instanceof java.util.Collection) {
            java.util.Collection<?> collection = (java.util.Collection<?>) obj;
            return obj.getClass().getSimpleName() + "[size=" + collection.size() + "]";
        }

        return obj.getClass().getSimpleName() + "[UNSERIALIZABLE]";
    }

    private boolean isEntity(Object obj) {
        String className = obj.getClass().getName();
        return className.startsWith("com.infina.hissenet.entity.");
    }

    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"Failed to serialize log object\",\"reason\":\"" + e.getMessage() + "\"}";
        }
    }

    private Object[] maskSensitiveData(Object[] args) {
        Object[] masked = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];

            if (arg == null) {
                masked[i] = null;
            } else if (arg instanceof String str && isSensitiveField(str)) {
                masked[i] = MASK;
            } else if (isPojo(arg)) {
                Object maskedPojo = safeMaskPojo(arg);
                masked[i] = maskedPojo != null ? maskedPojo : UNLOGGABLE;
            } else {
                masked[i] = arg;
            }
        }
        return masked;
    }

    private boolean isPojo(Object obj) {
        String pkg = obj.getClass().getPackageName();
        return pkg.startsWith("com.infina.hissenet.dto.request");
    }

    private Object safeMaskPojo(Object obj) {
        try {
            Class<?> clazz = obj.getClass();
            Object clone = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (isSensitiveField(field.getName())) {
                    field.set(clone, MASK);
                } else {
                    field.set(clone, value);
                }
            }
            return clone;
        } catch (Exception e) {
            logger.warn("Cannot mask POJO of type {}. Sensitive fields will be excluded from log.", obj.getClass().getName());
            return null;
        }
    }

    private boolean isSensitiveField(String fieldName) {
        return SENSITIVE_FIELD_NAMES.contains(fieldName.toLowerCase());
    }
}
