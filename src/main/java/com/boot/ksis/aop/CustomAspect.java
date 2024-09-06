package com.boot.ksis.aop;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Log.ActivityLog;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.log.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Aspect
@RequiredArgsConstructor
public class CustomAspect {
    private final AccountRepository accountRepository;
    private final ActivityLogRepository activityLogRepository;
    @Around("@annotation(CustomAnnotation)")
    public Object activityLogTrigger(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        CustomAnnotation customAnnotation = method.getAnnotation(CustomAnnotation.class);

        String activityDetail = customAnnotation.activityDetail();

        addActivityLog(activityDetail);

        return joinPoint.proceed();
    }

    private void addActivityLog(String activityDetail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Account account = accountRepository.findByAccountId(authentication.getName()).orElse(null);
        ActivityLog activityLog = ActivityLog.builder().account(account).activityDetail(activityDetail).dateTime(LocalDateTime.now()).build();
        activityLogRepository.save(activityLog);
    }
}
