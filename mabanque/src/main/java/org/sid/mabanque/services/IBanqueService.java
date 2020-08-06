package org.sid.mabanque.services;

import org.sid.mabanque.entities.Compte;
import org.sid.mabanque.entities.Operation;
import org.springframework.data.domain.Page;

public interface IBanqueService {

    public Compte consulterCompte(String codeCpte);
    public void verser(String codeCpte, double montant);
    public void retirer(String codeCpte, double montant);
    public void virement(String codeCpteEnvoyeur, String codeCpteDestinataire, double montant);
    public Page<Operation> listOperation(String codeCpte, int page, int size);
}
