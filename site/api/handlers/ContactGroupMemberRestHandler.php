<?php

    class ContactGroupMemberRestHandler
    {
        public function memberList($id)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "SELECT id, first_name, last_name, email FROM user WHERE id IN (SELECT user from contact_group_member WHERE group_id=".$id.") ORDER BY first_name, last_name";

            if($result = $connection->query($query))
            {
                while($row = $result->fetch_assoc())
                {
                    $response[] = $row;
                }
                $result->close();
                
                if(!isset($response) || $response === NULL)
                {
                    $response[] = array("empty" => "no members found"); 
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function add($group, $user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "INSERT INTO contact_group_member (group_id, user) VALUES (".$group.", ".$user.")";

            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $query = "SELECT * FROM contact_group_member WHERE group_id=".$group." AND user=".$user;

                    if($result = $connection->query($query))
                    {
                        while($row = $result->fetch_assoc())
                        {
                            $response[] = $row;
                        }
                        $result->close();
                    }
                    else {
                        $response[] = array("error" => $connection->error);
                    }
    
                }
                else
                {
                    $response[] = array("failure" => "member not added");
                }
            }
            else
            {
                $response[] = array("error" => $connection->error);
            }

            echo json_encode($response);
            $connection->close();
        }

        public function remove($group, $user)
        {
            $connection = new mysqli("cs4474-debt-ledger.chv9hyuyepg2.us-east-2.rds.amazonaws.com", "admin", "I6leZnstPdI7SSqameT4", "debt_ledger");
            $query = "DELETE FROM contact_group_member WHERE group_id=".$group." AND user=".$user;
            
            if($result = $connection->query($query))
            {
                if($connection->affected_rows > 0)
                {
                    $response[] = array("success" => "member removed");
                }
                else
                {
                    $response[] = array("failure" => "member not removed");
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