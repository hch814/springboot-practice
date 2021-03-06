package com.hch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;

@ConfigurationProperties(prefix = "custom")
@Getter
@Setter
public class CustomProperties {
    private Person author;
    private String projectName;

    public static class Person {
        private String name;
        private Integer age;
        private Date birthday;
        private boolean male;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public boolean isMale() {
            return male;
        }

        public void setMale(boolean male) {
            this.male = male;
        }
    }
}
