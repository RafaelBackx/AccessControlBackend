package hhh.acs.rest;

import hhh.acs.model.BiostarAPIRequests;
import hhh.acs.model.Mode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@RestController()
@RequestMapping("/biostar")
public class BiostarREST {

    private BiostarAPIRequests biostarAPIRequests = new BiostarAPIRequests("https://localhost");

    @GetMapping("/login")
    public void login() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        String result = biostarAPIRequests.logIn("admin","t");
        System.out.println(result);
    }

    @GetMapping("/lock")
    public void lock() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.LOCK);
    }

    @GetMapping("/unlock")
    public void unlock() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.UNLOCK);
    }

    @GetMapping("/release")
    public void release() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        int[] ids = {2};
        biostarAPIRequests.lockUnlockReleaseDoor(ids, Mode.RELEASE);
    }
}
