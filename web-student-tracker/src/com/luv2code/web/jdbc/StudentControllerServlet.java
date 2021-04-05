package com.luv2code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private StudentDbUtil studentDbUtil; 
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	
	//Chamado pelo servidor quando o Servlet é inicializado pela primeira vez
	//Similar ao construtor do Servlet
	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
		}catch(Exception ex){
			throw new ServletException(ex);
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		try {
			//read the command parameter
			String theCommand = request.getParameter("command");
			
			if(theCommand==null) {
				theCommand = "LIST";
			}
			
			//route to appropriate method
			switch(theCommand) {
			case "LIST":
				listStudents(request,response);
				break;
				
			case "ADD":
				addStudent(request,response);
				break;
				
			case "LOAD":
				loadStudent(request, response);
				break;
			
			case "UPDATE":
				updateStudent(request, response);
				break;
			
			case "DELETE":
				deleteStudent(request, response);
				break;
				
			default:
				listStudents(request,response);
			}
			
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
	}


	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		// read student info from form data
		String id = request.getParameter("studentId");
		
		//perform delete on database
		studentDbUtil.deleteStudent(id);
		
		//send user back to list-students page
		listStudents(request,response);
		
	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		// read student info from form data
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create a new student object
		Student student = new Student(id, firstName, lastName, email);
		
		//perform update on database
		studentDbUtil.updateStudent(student);
		
		//send user back to list-students page
		listStudents(request,response);
		
	}


	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//read id from form data
		String studentId = request.getParameter("studentId");
		
		//get the student from database
		Student student = studentDbUtil.getStudent(studentId);
		
		//place the student in the request attribute
		request.setAttribute("THE_STUDENT", student);
		
		//send to jsp page
		RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
		dispatcher.forward(request, response);
	}


	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read student info from form data
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create new student
		Student student = new Student(firstName, lastName, email);
		
		//add the student to database
		studentDbUtil.addStudent(student);
		//send back to main page
		
		listStudents(request, response); 
		
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//get students from db util
		List<Student> students = studentDbUtil.getStudents();
		
		//add students to the request
		request.setAttribute("STUDENT_LIST", students);
		
		//send to JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
