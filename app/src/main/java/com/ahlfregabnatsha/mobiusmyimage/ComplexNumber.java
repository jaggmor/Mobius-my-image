package com.ahlfregabnatsha.mobiusmyimage;

//
//Creates object representing a complex
// number with regular operations from mathematics.
//

// Todo Rename functions for mor readable code
// I.e. z.Re() and static ComplexNumber.Re(z)
import androidx.annotation.NonNull;


public class ComplexNumber {
    protected double real;
    protected double imaginary;


    public ComplexNumber(double x, double y) {
        this.real = x;
        this.imaginary = y;
    }

    public ComplexNumber(int x, int y) {
        this.real = x;
        this.imaginary = y;
    }

    @Override
    @NonNull
    public String toString() {
        return this.real + " + i*" + this.imaginary;
    }

    //Getters and setters.
    public double getReal() {
        return this.real;
    }

    public double getImaginary() {
        return this.imaginary;
    }

    public double getModulus() {
        return Math.sqrt(this.real*this.real + this.imaginary*this.imaginary);
    }

    public double getModulusSquared() {
        return this.real*this.real + this.imaginary*this.imaginary;
    }

    public double getArgumentRadians() {
        return Math.atan(this.imaginary/this.real);
    }

    public void setReal(double x) {
        this.real = x;
    }

    public void setImaginary(double y) {
        this.imaginary = y;
    }

    public static ComplexNumber add(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.real+z2.real, z1.imaginary+z2.imaginary);
    }

    public static ComplexNumber subtract(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.real-z2.real, z1.imaginary-z2.imaginary);
    }

    public static ComplexNumber multiplyByScalar(ComplexNumber z, double lambda) {
        return new ComplexNumber(z.real*lambda, z.imaginary*lambda);
    }

    public static ComplexNumber multiply(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.real*z2.real - z1.imaginary*z2.imaginary,
                z1.real*z2.imaginary + z1.imaginary*z2.real);
    }

    public static ComplexNumber conjugate(ComplexNumber z) {
        return new ComplexNumber(z.real, -z.imaginary);
    }

    public static ComplexNumber divide(ComplexNumber numerator, ComplexNumber denominator) {
        ComplexNumber z = multiply(numerator, conjugate(denominator));
        return new ComplexNumber(z.real/denominator.getModulusSquared(),
                z.imaginary/denominator.getModulusSquared());
    }
}
