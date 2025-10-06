/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.garaje.facade;

/** Excepción de negocio (mensajes para el usuario). */
public class BusinessException extends Exception {
    public BusinessException(String message) { super(message); }
}
