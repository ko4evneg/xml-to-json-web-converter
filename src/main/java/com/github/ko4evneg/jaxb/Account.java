package com.github.ko4evneg.jaxb;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="account", namespace = "wsapi:Utils")
@XmlAccessorType(XmlAccessType.NONE)
public class Account {
    @XmlAttribute(name = "type")
    public String type;
    @XmlValue
    public String value;
}
