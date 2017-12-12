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
public class NotEnoughServersException extends Exception {

    /**
     * Creates a new instance of <code>NotEnoughServersException</code> without
     * detail message.
     */
    public NotEnoughServersException() {
    }

    /**
     * Constructs an instance of <code>NotEnoughServersException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotEnoughServersException(String msg) {
        super(msg);
    }
}
