package com.example.patorestaurant.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String text;

    @Column
    private Date date;

    @ManyToOne
    private Blog blog;

}
