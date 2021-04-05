package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;

	public StudentDbUtil(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<Student> getStudents() throws Exception {

		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {
			// get a connection
			myConn = dataSource.getConnection();
			// create sql statement
			String sql = "select * from student order by last_name";

			myStmt = myConn.createStatement();
			// execute query
			myRs = myStmt.executeQuery(sql);

			// proccess result set
			while (myRs.next()) {
				// retrive result from row
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				// add it to the students list
				students.add(tempStudent);
			}

			return students;
		} finally {
			// close jdbc objects
			close(myConn, myStmt, myRs);
		}
	}

	public void addStudent(Student student) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			// get db connection
			myConn = dataSource.getConnection();
			// Create sql statement
			String sql = "insert into student " + "(first_Name, last_Name, email) " + "values (?,?,?)";
			myStmt = myConn.prepareStatement(sql);

			// set the param values
			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());

			// execute sql
			myStmt.execute();
		} finally {
			// clean jdbc objects
			close(myConn, myStmt, null);
		}

	}

	public Student getStudent(String theStudentId) throws Exception {
		Student student = null;

		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;

		int studentId;
		try {

			// Convert student id to int
			studentId = Integer.parseInt(theStudentId);

			// get connection to db
			myConn = dataSource.getConnection();

			// create sql
			String sql = "select * from student where id=?";

			// create prepared statement
			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, studentId);

			// execute statement
			myRs = myStmt.executeQuery();

			// retrieve data from row
			if (myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				student = new Student(studentId, firstName, lastName, email);
			} else {
				throw new Exception("Não foi possível encontrar aluno com id: " + studentId);
			}

			return student;
		} finally {
			close(myConn, myStmt, myRs);
		}

	}

	public void updateStudent(Student student) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			// get db connection
			myConn = dataSource.getConnection();

			// create sql query
			String sql = "update student set first_name=?, last_name=?, email=? where id=?";

			// prepare statement
			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());
			myStmt.setInt(4, student.getId());

			// execute sql query
			myStmt.execute();

		} finally {
			close(myConn, myStmt, null);
		}

	}

	public void deleteStudent(String id) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			
			//Convert student id to int
			int studentId = Integer.parseInt(id);
			
			// get db connection
			myConn = dataSource.getConnection();

			// create sql query
			String sql = "delete from student where id=?";

			// prepare statement
			myStmt = myConn.prepareStatement(sql);

			// set params
			myStmt.setInt(1, studentId);

			// execute sql query
			myStmt.execute();

		} finally {
			close(myConn, myStmt, null);
		}

	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if (myRs != null) {
				myRs.close();
			}

			if (myStmt != null) {
				myStmt.close();
			}

			if (myConn != null) {
				myConn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
