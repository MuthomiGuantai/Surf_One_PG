package com.surfonepg.radius.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "radcheck")
public class RadCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String op = "==";

    @Column(nullable = false)
    private String value;

    protected RadCheck() {}

    public RadCheck(String username, String attribute, String op, String value) {
        this.username = username;
        this.attribute = attribute;
        this.op = op;
        this.value = value;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAttribute() { return attribute; }
    public String getOp() { return op; }
    public String getValue() { return value; }
}
