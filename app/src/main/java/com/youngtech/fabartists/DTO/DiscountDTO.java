package com.youngtech.fabartists.DTO;

import java.io.Serializable;
/**
 * Created by VARUN on 01/01/19.
 */
public class DiscountDTO implements Serializable {

    String code = "";
    String description = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
