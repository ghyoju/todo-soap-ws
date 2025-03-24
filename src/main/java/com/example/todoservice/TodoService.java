package com.example.todoservice;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface TodoService {
    @WebMethod
    String addTodo(String task);
    
    @WebMethod
    String getTodo(int id);
}