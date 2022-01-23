package com.github.ko4evneg.jaxb;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.NONE)
public class Envelope {
    @XmlElement(name = "Body")
    public Body body;
}
