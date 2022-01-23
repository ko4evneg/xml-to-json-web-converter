package com.github.ko4evneg.jaxb;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "Body")
@XmlAccessorType(XmlAccessType.NONE)
public class Body {
    @XmlElement(name = "sendPayment", namespace = "wsapi:Payment")
    public SendPayment sendPayment;
}
