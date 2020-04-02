<?php

    class ContactRestHandler
    {
        public function id($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT contact from contact WHERE user=".$id.") ORDER BY first_name, last_name";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no contacts found"); 
                }
            } else {
                 $response[] = array("error" => $connection->error); 
            }

            echo json_encode($response);
            $connection->close();
        }

        /*
        public function add($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "INSERT INTO contact (user, contact) VALUES user=".$user.", contact=".$contact;
            $query2 = "INSERT INTO contact (user, contact) VALUES user=".$contact.", contact=".$user;

            if (($connection->query($query) === TRUE) && ($connection->query($query2) === TRUE))
            {
                if($result->affected_rows > 0)
                {
                    $response[] = array("result" => "contacts added successfully");
                }
                else
                {
                    $response[] = array("result" => $connection->error);
                }
            }

            echo json_encode($response);
            $result->close();
            $connection->close();
        }
        */

        public function delete($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact WHERE (user=".$user." AND contact=".$contact.") OR (user=".$contact." AND contact=".$user.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "contacts deleted");
                }
                else
                {
                    $response[] = array("failure" => "contacts not deleted");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }
    }
?>