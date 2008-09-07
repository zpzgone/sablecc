/* This file was generated by SableCC's ObjectMacro. */

package org.sablecc.objectmacro.macro;

public class M_expand_append_after_last_string_part
        extends Macro {

    // ---- constructor ----

    M_expand_append_after_last_string_part(
            Macro parent,
            String p_after_last_string_part) {

        this.parent = parent;
        this.p_after_last_string_part = p_after_last_string_part;
    }

    // ---- parent ----

    private final Macro parent;

    @Override
    Macro get_parent() {

        return this.parent;
    }

    // ---- parameters ----

    private final String p_after_last_string_part;

    String get_local_p_after_last_string_part() {

        return this.p_after_last_string_part;
    }

    // ---- parameter accessors ----

    private String cached_p_after_last_string_part;

    private String get_p_after_last_string_part() {

        String result = this.cached_p_after_last_string_part;

        if (result == null) {
            Macro current = this;

            while (!(current instanceof M_expand_append_after_last_string_part)) {
                current = current.get_parent();
            }

            result = ((M_expand_append_after_last_string_part) current)
                    .get_local_p_after_last_string_part();
            this.cached_p_after_last_string_part = result;
        }

        return result;
    }

    // ---- appendTo ----

    @Override
    public void appendTo(
            StringBuilder sb) {

        sb.append("      sb.append(\"");
        sb.append(get_p_after_last_string_part());
        sb.append("\");");
        sb.append(EOL);
    }

}