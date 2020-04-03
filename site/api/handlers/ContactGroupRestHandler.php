<?php

    class ContactGroupRestHandler
    {
        public function groupList($user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT contact_group.id, name, JSON_ARRAYAGG(JSON_OBJECT('id', user.id, 'first_name', user.first_name, 'last_name', user.last_name, 'email', user.email)) AS members FROM contact_group INNER JOIN contact_group_member ON contact_group.id = contact_group_member.group_id INNER JOIN user ON contact_group_member.user = user.id WHERE owner = ".$user." GROUP BY contact_group.id";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no groups found");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function group($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM contact_group WHERE id=".$id;

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();

                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("missing" => "group not found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function add($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $info = json_decode($input);
            $query = "INSERT INTO contact_group (name, owner) VALUES ('".$info->name."', ".$info->owner.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_group WHERE id=".$connection->insert_id;

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
                    $response[] = array("failure" => "group not added");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function update($id, $name)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "UPDATE contact_group SET name='".$name."' WHERE id=".$id;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    
                     $response[] = array("success" => "group updated");
                }
                else
                {
                    $response[] = array("failure" => "group not updated");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function delete($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact_group WHERE id=".$id;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "group deleted");
                }
                else
                {
                    $response[] = array("failure" => "group not deleted");
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