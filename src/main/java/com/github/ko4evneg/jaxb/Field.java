package com.github.ko4evneg.jaxb;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="field")
@XmlAccessorType(XmlAccessType.NONE)
public class Field {
    @XmlAttribute(name = "id")
    public String id;
    @XmlAttribute(name = "value")
    public String value;
}
