package com.goldgyro.platform.core.comm.utils;

import java.beans.PropertyEditorSupport;

import org.springframework.web.util.HtmlUtils;

/**
 * wg2993
 */
public class StringEscapeEditorUtils extends PropertyEditorSupport {
    private boolean escapeHTML;// 编码HTML
    private boolean escapeJavaScript;// 编码JavaScript

    public StringEscapeEditorUtils() {
        super();
    }

    public StringEscapeEditorUtils(boolean escapeHTML, boolean escapeJavaScript) {
        super();
        this.escapeHTML = escapeHTML;
        this.escapeJavaScript = escapeJavaScript;
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        } else {
            String value = text;
            if (escapeHTML) {
                value = HtmlUtils.htmlEscape(value);
//                logger.debug("value:" + value);
            }
            if (escapeJavaScript) {
//                value = StringEscapeUtils.escapeJavaScript(value);
//                logger.debug("value:" + value);
            }
            setValue(value);
        }
    }
}
