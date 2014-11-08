package edu.mit.csail.sdg.squander.eventbased;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.mit.csail.sdg.annotations.Options;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
@Documented
public @interface StateOptions {
    Options value();
}
