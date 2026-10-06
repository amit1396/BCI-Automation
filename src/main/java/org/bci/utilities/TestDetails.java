package org.bci.utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TestDetails {
    
    String id();
    String author(); 
    
    Priority priority() default Priority.MEDIUM;
    Severity severity() default Severity.NORMAL;
    
    String[] browsers() default {"chrome"};
    String device() default "Desktop";

    enum Priority {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }

    enum Severity {
        BLOCKER,
        CRITICAL,
        NORMAL,
        MINOR,
        TRIVIAL
    }
}