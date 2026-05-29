package com.hyperreset.api.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject the currently authenticated user's ID into controller method parameters.
 * <p>
 * Usage:
 * <pre>
 * &#64;GetMapping("/profile")
 * public ResponseEntity<?> getProfile(&#64;CurrentUser Long userId) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
