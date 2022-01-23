package com.github.ko4evneg.jaxb;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "sendPayment", namespace = "wsapi:Payment")
@XmlAccessorType(XmlAccessType.FIELD)
public class SendPayment {
    public String token;
    public String cardNumber;
    public String requestId;
    public String amount;
    public String currency;
    @XmlElement(name="account", namespace = "wsapi:Utils")
    public List<Account> account;
    public String page;
    public List<Field> field;
}
