    package com.ensias.essuhelp;

    public class User {
        private String name;
        private String studentId;
        private String email;
        private String role;
        private String medicalInfo;


        public User() {
        }

        public User(String name, String studentId, String email, String role) {
            this.name = name;
            this.studentId = studentId;
            this.email = email;
            this.role = role;
            this.medicalInfo = "";
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getMedicalInfo() {
            return medicalInfo;
        }

        public void setMedicalInfo(String medicalInfo) {
            this.medicalInfo = medicalInfo;
        }
    }
