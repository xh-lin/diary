package com.xuhuang.diary.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {

    @Column(nullable = false)
    @NonNull
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @JsonIdentityInfo( // Jackson only serialize id
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    @NonNull
    private User user;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @NonNull
    private Set<Record> records = new HashSet<>();

}
