<?php

    class ContactRequestRestHandler
    {
        public function pending($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT user from contact_request WHERE contact=".$id." AND status='pending')";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no requests found");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function pendingFromUser($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT contact from contact_request WHERE user=".$id." AND status='pending')";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no requests found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }
        
        public function add($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "INSERT INTO contact_request (user, contact) VALUES (".$user.", ".$contact.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_request WHERE user=".$user." AND contact=".$contact;
                    
                    if($result = $connection->query($query))
                    {
                        while($row = $result->fetch_assoc())
                        {
                            $response[] = $row;
                        }
                        $result->close();
                    }
                    else
                    {
                        $response[] = array("error" => $connection->error);
                    }
                }
                else
                {
                    $response[] = array("failure" => "contact request not added");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function delete($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact_request WHERE user=".$user." AND contact=".$contact;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "contact request deleted");
                }
                else
                {
                    $response[] = array("failure" => "contact request not deleted");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function accept($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "UPDATE contact_request SET status='accepted' WHERE user=".$user." AND contact=".$contact;
            
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "INSERT INTO contact (user, contact) VALUES (".$user.", ".$contact.")";
                    $query2 = "INSERT INTO contact (user, contact) VALUES (".$contact.", ".$user.")";
                
                    if(($result = $connection->query($query)) && ($result2 = $connection->query($query2)))
                    {
                        $query = "SELECT * FROM contact_request WHERE user=".$user." AND contact=".$contact;

                        if($result = $connection->query($query))
                        {
                            while($row = $result->fetch_assoc())
                            {
                                $response[] = $row;
                            }
                            $result->close();
                        }
                        else
                        {
                            $response[] = array("error" => $connection->error);
                        }
                    }
                    else
                    {
                        $response[] = array("error" => $connection->error);
                    }
                }
                else
                {
                    $response[] = array("failure" => "request not found");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function deny($user, $contact)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "UPDATE contact_request SET status='denied' WHERE user=".$user." AND contact=".$contact;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "request denied");
                }
                else
                {
                    $response[] = array("failure" => "request not found");
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