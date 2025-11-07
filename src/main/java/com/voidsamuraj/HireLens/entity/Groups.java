package com.voidsamuraj.HireLens.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLInsert;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
@Data
@SQLInsert(sql = "INSERT INTO groups (group_name, skill) VALUES (?, ?) ON CONFLICT (skill) DO NOTHING")
public class Groups {

    @Id
    @Column(name = "skill", nullable = false)
    private String skill;

    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;
}
