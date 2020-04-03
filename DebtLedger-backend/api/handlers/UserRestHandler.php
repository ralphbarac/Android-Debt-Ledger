<?php

    class UserRestHandler
    {
        public function login($email, $pass)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT * FROM user WHERE email='".$email."' AND password='".$pass."'";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("missing" => "user not found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function getUserById($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$id;
            
            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("missing" => "user not found");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function getUserByEmail($email)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE email='".$email."'";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("missing" => "user not found");
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
            $query = "INSERT INTO user (first_name, last_name, email, password) VALUES ('".$info->first_name."', '".$info->last_name."', '".$info->email."', '".$info->password."')";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$connection->insert_id;

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
                    $response[] = array("failure" => "user not added");
                }
            }
            else
            {
                if (strcmp($connection->error, "Duplicate entry '".$info->email."' for key 'email'") == 0)
                {
                    $response[] = array("duplicate" => "email in use");
                }
                else
                {
                    $response[] = array("error" => $connection->error);
                }
            }

            echo json_encode($response);
            $connection->close();
        }

        public function update($input)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $info = json_decode($input);
            $query = "UPDATE user SET first_name='".$info->first_name."', last_name='".$info->last_name."', email='".$info->email."', password='".$info->password."' WHERE id=".$info->id;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT id, first_name, last_name, email FROM user WHERE id=".$info->id;

                    if($result = $connection->query($query)) {
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
                    $response[] = array("failure" => "user not updated");
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
            $query = "DELETE FROM user WHERE id=".$id;

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "user deleted");
                }
                else
                {
                    $response[] = array("failure" => "user not deleted");
                }
            } else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }
    }
?>