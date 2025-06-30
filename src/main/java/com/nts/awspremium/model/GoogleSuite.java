package com.nts.awspremium.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "google_suite")
public class GoogleSuite implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    @Column(columnDefinition = "TINYINT default 0")
    private Boolean status=false;
    @Column(columnDefinition = "TINYINT default 0")
    private Boolean state=false;
    @Column(columnDefinition = "bigint default 0")
    private Long update_time=0L;

    public GoogleSuite() {
    }
}

