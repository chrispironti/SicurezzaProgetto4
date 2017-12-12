/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sicurezzaprogetto4;

/**
 *
 * @author gennaroavitabile
 */
public class TooBigSecretException extends Exception {

    /**
     * Creates a new instance of <code>TooBigSecretException</code> without
     * detail message.
     */
    public TooBigSecretException() {
    }

    /**
     * Constructs an instance of <code>TooBigSecretException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TooBigSecretException(String msg) {
        super(msg);
    }
}
