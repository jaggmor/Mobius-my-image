package com.ahlfregabnatsha.mobiusmyimage;

import androidx.annotation.NonNull;

// Todo Make a separate Determinant class to use here.


public class MobiusTransformation {

    //Mobius transformation coefficients.
    protected ComplexNumber a;
    protected ComplexNumber b;
    protected ComplexNumber c;
    protected ComplexNumber d;

    public MobiusTransformation(ComplexNumber a, ComplexNumber b, ComplexNumber c, ComplexNumber d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    // z1, z2, z3 maps to w1, w2, w3 respectively.
    public MobiusTransformation(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3,
                                ComplexNumber w1, ComplexNumber w2, ComplexNumber w3) {
        this.a = calculate_a(z1, z2, z3, w1, w2, w3);
        this.b = calculate_b(z1, z2, z3, w1, w2, w3);
        this.c = calculate_c(z1, z2, z3, w1, w2, w3);
        this.d = calculate_d(z1, z2, z3, w1, w2, w3);
    }

    @Override
    @NonNull
    public String toString() {
        return "MobiusTransformation{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                ", d=" + d +
                '}';
    }

    public ComplexNumber transform(ComplexNumber z) {
        return ComplexNumber.divide(ComplexNumber.add(ComplexNumber.multiply(a, z), b),
                ComplexNumber.add(ComplexNumber.multiply(c, z), d));    //(az + b)/(cz + d)
    }

    public ComplexNumber transformInverse(ComplexNumber z){
        return ComplexNumber.divide(ComplexNumber.subtract(ComplexNumber.multiply(d, z), b),
                ComplexNumber.add(ComplexNumber.multiply(ComplexNumber.multiplyByScalar(c, -1), z), a));    //(dz - b)/(-cz + a)
    }

    //Calculate coefficients a, b, c and d with explicit determinant formula.
    //Determinants are coded explicitly. Improve?!
    public ComplexNumber calculate_a(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3,
                                     ComplexNumber w1, ComplexNumber w2, ComplexNumber w3) {
        //1*(z2w2w3 - z3w3w2) - 1*(z1w1w3 - w1w3z3) + 1*(z1w1w2 - w1z2w2)
        ComplexNumber expansion1 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),w3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, w3),w2)
                );
        ComplexNumber expansion2 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),w3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, w1),w3)
        );
        ComplexNumber expansion3 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),w2),
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),w1)
        );
        return ComplexNumber.add(ComplexNumber.subtract(expansion1,expansion2),
                expansion3);
    }

    public ComplexNumber calculate_b(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3,
                                     ComplexNumber w1, ComplexNumber w2, ComplexNumber w3) {
        //w1*(z2w2z3 - z3w3z2) - w2*(z1w1z3 - z1w3z3) + w3*(z1w1z2 - z1z2w2)
        ComplexNumber expansion1 = ComplexNumber.multiply(w1,ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),z3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, w3),z2))
        );
        ComplexNumber expansion2 = ComplexNumber.multiply(w2,ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),z3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, z1),w3))
        );
        ComplexNumber expansion3 = ComplexNumber.multiply(w3,ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),z2),
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),z1))
        );
        return ComplexNumber.add(ComplexNumber.subtract(expansion1,expansion2),
                expansion3);
    }

    public ComplexNumber calculate_c(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3,
                                     ComplexNumber w1, ComplexNumber w2, ComplexNumber w3) {
        //1*(z2w3 - z3w2) - 1*(z1w3 - w1z3) + 1*(z1w2 - w1z2)
        ComplexNumber expansion1 = ComplexNumber.subtract(
                ComplexNumber.multiply(z2,w3), ComplexNumber.multiply(z3,w2)
        );
        ComplexNumber expansion2 = ComplexNumber.subtract(
                ComplexNumber.multiply(z1,w3), ComplexNumber.multiply(z3,w1)
        );
        ComplexNumber expansion3 = ComplexNumber.subtract(
                ComplexNumber.multiply(z1,w2), ComplexNumber.multiply(z2,w1)
        );
        return ComplexNumber.add(ComplexNumber.subtract(expansion1,expansion2),
                expansion3);
    }

    public ComplexNumber calculate_d(ComplexNumber z1, ComplexNumber z2, ComplexNumber z3,
                                     ComplexNumber w1, ComplexNumber w2, ComplexNumber w3) {
        //1*(z2w2z3 - z3w3z2) - 1*(z1w1z3 - z1w3z3) + 1*(z1w1z2 - z1z2w2)
        ComplexNumber expansion1 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),z3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, w3),z2)
        );
        ComplexNumber expansion2 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),z3),
                ComplexNumber.multiply(ComplexNumber.multiply(z3, z1),w3)
        );
        ComplexNumber expansion3 = ComplexNumber.subtract(
                ComplexNumber.multiply(ComplexNumber.multiply(z1, w1),z2),
                ComplexNumber.multiply(ComplexNumber.multiply(z2, w2),z1)
        );
        return ComplexNumber.add(ComplexNumber.subtract(expansion1,expansion2),
                expansion3);
    }
}
