/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 *
 * Author: Nikolay Gorodnov
 * Created: 19.12.2008 14:16:24
 * $Id$
 */
package com.haulmont.cuba.toolkit.gwt.client.impl;

public class ToolsImpl {

    public native int parseSize(String s) /*-{
         try {
            var result = /^(\d+)(%|px|em|ex|in|cm|mm|pt|pc)$/.testexec(s);
            return parseInt(result[0]);
         } catch (e) {
            return -1;
         }
    }-*/;

    public native String format(String message) /*-{
        return message.replace(/\[br\]/, "<br/>")
                .replace(/\[b\]/, "<b>")
                .replace(/\[\/b\]/, "</b>")
                .replace(/\[i\]/, "<i>")
                .replace(/\[\/i\]/, "</i>");
    }-*/;

}
