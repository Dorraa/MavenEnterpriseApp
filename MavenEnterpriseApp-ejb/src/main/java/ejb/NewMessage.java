/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author dorra
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jmsdemo/mymsgdest"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class NewMessage implements MessageListener {
    @Resource private MessageDrivenContext mdc;
    @PersistenceContext(unitName = "com.mycompany_MavenEnterpriseApp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;
    public NewMessage() {
    }
    
    @Override
    public void onMessage(Message message) {
        ObjectMessage msg = null;
        try { if (message instanceof ObjectMessage) {
        msg = (ObjectMessage) message;
        NewsEntity e = (NewsEntity) msg.getObject();
        save(e); }
                } catch (JMSException e) { e.printStackTrace();
        mdc.setRollbackOnly();
        } catch (Throwable te) {
        te.printStackTrace();}
    }

    public void save(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }
    
}
