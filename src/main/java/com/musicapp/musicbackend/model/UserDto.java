package com.musicapp.musicbackend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "UserDto")
public class UserDto implements Serializable {
        @Getter
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @NotBlank(message = "Enter your Email")
        @Email(message = "Enter an valid Email address")
        private String email;
        @NotBlank(message = "Enter your password")
        @Length(min = 6, message = "Password must be at least 6 characters")
        private String password;
        @NotBlank(message = "Enter your Firstname")
        private String firstname;
        @NotBlank(message = "Enter your Lastname")
        private String lastname;

        public UserDto(){
        }

        public void setId(Long id) {
                this.id = id;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public void setFirstname(String firstname) {
                this.firstname = firstname;
        }

        public void setLastname(String lastname) {
                this.lastname = lastname;
        }

        @Override
        public String toString() {
                return "UserDto{" +
                        "id=" + id +
                        ", email='" + email + '\'' +
                        ", password='" + password + '\'' +
                        ", firstname='" + firstname + '\'' +
                        ", lastname='" + lastname + '\'' +
                        '}';
        }
}

