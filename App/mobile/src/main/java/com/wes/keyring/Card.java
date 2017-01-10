package com.wes.keyring;

public class Card {

    private int _id;
    private String _cardName;
    private String _cardHolder;
    private String _barcodeFormat;
    private String _serialNumber;

    public Card () {
    }

    public Card (String cardName, String cardHolder, String barcodeFormat, String serialNumber) {
        this._cardName = cardName;
        this._cardHolder = cardHolder;
        this._barcodeFormat = barcodeFormat;
        this._serialNumber = serialNumber;
    }

    public void set_id (int _id) {
        this._id = _id;
    }

    public void set_cardName (String _cardName) {
        this._cardName = _cardName;
    }

    public void set_cardHolder (String _cardHolder) {
        this._cardHolder = _cardHolder;
    }

    public void set_barcodeFormat (String _barcodeFormat) {
        this._barcodeFormat = _barcodeFormat;
    }

    public void set_serialNumber (String _serialNumber) {
        this._serialNumber = _serialNumber;
    }

    public int get_id () {
        return _id;
    }

    public String get_cardName () {
        return _cardName;
    }

    public String get_cardHolder() {
        return _cardHolder;
    }

    public String get_barcodeFormat() {
        return _barcodeFormat;
    }

    public String get_serialNumber() {
        return _serialNumber;
    }

    @Override
    public String toString() {
        return _serialNumber;
    }
}
