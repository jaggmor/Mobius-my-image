package com.ahlfregabnatsha.mobiusmyimage;

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


    //Getters and setters.
    public double getReal() {
        return this.real;
    }

    public double getImaginary() {
        return this.imaginary;
    }

    public void setReal(double x) {
        this.real = x;
    }

    public void setImaginary(double y) {
        this.imaginary = y;
    }

    public static ComplexNumber add(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.getReal()+z2.getReal(), z1.getImaginary()+z2.getImaginary());
    }

    public static ComplexNumber multiplyByScalar(ComplexNumber z, double lambda) {
        return new ComplexNumber(z.getReal()*lambda, z.getImaginary()*lambda);
    }

    public static ComplexNumber multiply(ComplexNumber z1, ComplexNumber z2) {
        return new ComplexNumber(z1.getReal()*z2.getReal() - z1.getImaginary()*z2.getImaginary(),
                z1.getReal()*z2.getImaginary() + z1.getImaginary()*z2.getReal());
    }

    public static ComplexNumber conjugate(ComplexNumber z) {
        return new ComplexNumber(z.getReal(), -z.getImaginary());
    }

    public double getModulus() {
        return Math.sqrt(this.getReal()*this.getReal() + this.getImaginary()*this.getImaginary());
    }

    public double getModulusSquared() {
        return this.getReal()*this.getReal() + this.getImaginary()*this.getImaginary();
    }

    public double getArgumentRadians() {
        return Math.atan(this.getImaginary()/this.getImaginary());
    }

    public static ComplexNumber divide(ComplexNumber numerator, ComplexNumber denominator) {
        ComplexNumber z = multiply(numerator, conjugate(denominator));
        return new ComplexNumber(z.getReal()/denominator.getModulusSquared(),
                z.getImaginary()/denominator.getModulusSquared());
    }
}
