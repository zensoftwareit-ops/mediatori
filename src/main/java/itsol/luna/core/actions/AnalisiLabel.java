package itsol.luna.core.actions;

import java.util.List;
import itsol.luna.core.beans.Customer;
import itsol.luna.core.beans.Product;
import itsol.luna.core.interfaces.MyActionInterface;
import itsol.luna.core.support.MyActionSupport;
import itsol.luna.core.support.SessionSupport;

public class AnalisiLabel extends MyActionSupport implements MyActionInterface {
	private static final long serialVersionUID = 1L;
	public String execute() { return SessionSupport.checkSession() ? SUCCESS : SUCCESS_NOLOGIN; }
	public List<Customer> getCustomers() { return SessionSupport.getCustomers(); }
	public List<Product> getProducts() { return SessionSupport.getProducts(); }
}
