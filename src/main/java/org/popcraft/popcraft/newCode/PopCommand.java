package org.popcraft.popcraft.newCode;

import java.lang.annotation.*;

/**
 * Created by Jonny on 12/2/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PopCommand {

    String value();

}
